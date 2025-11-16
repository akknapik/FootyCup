package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.tournament.CreateTournamentRequest;
import com.tournament.app.footycup.backend.dto.tournament.UpdateTournamentRequest;
import com.tournament.app.footycup.backend.enums.TournamentStatus;
import com.tournament.app.footycup.backend.enums.UserRole;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import com.tournament.app.footycup.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor
@Service
public class TournamentService {
    private static final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final TournamentRepository tournamentRepository;
    private final ScheduleService scheduleService;
    private final FormatService formatService;
    private final TeamService teamService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final AuthorizationService authorizationService;
    private final QrCodeService qrCodeService;

    @Transactional
    public Tournament createTournament(CreateTournamentRequest request, User organizer) {
        var tournament = new Tournament();
        tournament.setName(request.name());
        tournament.setStartDate(request.startDate());
        tournament.setEndDate(request.endDate());
        tournament.setLocation(request.location());
        tournament.setCode(generateUniqueTournamentCode());
        tournament.setOrganizer(organizer);
        tournament.setPublicVisible(request.publicVisible());
        var saved = tournamentRepository.save(tournament);
        scheduleService.createEmptySchedules(saved.getId(), organizer);
        return saved;
    }

    @Transactional(readOnly = true)
    public Tournament getTournamentById(Long id, User requester) {
        var tournament = tournamentRepository.findWithRefereesById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        authorizationService.ensureCanViewTournament(tournament, requester);
        refreshStatus(tournament);

        return tournament;
    }

    @Transactional
    public Tournament updateTournament(Long id, UpdateTournamentRequest updated, User organizer) {
        var tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);
        if (updated.name() != null) tournament.setName(updated.name());
        if (updated.startDate() != null) tournament.setStartDate(updated.startDate());
        if (updated.endDate() != null) tournament.setEndDate(updated.endDate());
        if (updated.location() != null) tournament.setLocation(updated.location());
        if (updated.publicVisible() != null) tournament.setPublicVisible(updated.publicVisible());
        return tournamentRepository.save(tournament);
    }

    @Transactional(readOnly = true)
    public List<Tournament> getTournamentsByOrganizer(User organizer) {
        if (organizer == null) {
            return List.of();
        }
        List<Tournament> tournaments = tournamentRepository.findByOrganizer(organizer);
        refreshStatuses(tournaments);
        return tournaments;
    }

    @Transactional(readOnly = true)
    public Tournament getTournamentByCode(String code) {
        var normalized = Optional.ofNullable(code)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Tournament code cannot be empty"));

        var tournament = tournamentRepository.findByCode(normalized)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        refreshStatus(tournament);
        return tournament;
    }

    @Transactional(readOnly = true)
    public List<Tournament> getAllTournamentsForAdmin(User user) {
        if (user == null || user.getUserRole() != UserRole.ADMIN) {
            return List.of();
        }
        var tournaments = tournamentRepository.findAll();
        refreshStatuses(tournaments);
        return tournaments;
    }

    @Transactional(readOnly = true)
    public List<Tournament> getTournamentsAsReferee(User referee) {
        if (referee == null) {
            return List.of();
        }
        var tournaments = tournamentRepository.findDistinctByReferees_Id(referee.getId());
        refreshStatuses(tournaments);
        return tournaments;
    }

    @Transactional(readOnly = true)
    public List<Tournament> getTournamentsAsCoach(User coach) {
        if (coach == null) {
            return List.of();
        }
        var teams = teamRepository.findByCoach_Id(coach.getId());
        var tournaments = teams.stream()
                .map(Team::getTournament)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        refreshStatuses(tournaments);
        return tournaments;
    }

    @Transactional(readOnly = true)
    public List<Tournament> getFollowedTournaments(User user) {
        if (user == null) {
            return List.of();
        }
        var tournaments = tournamentRepository.findDistinctByFollowers_Id(user.getId());
        refreshStatuses(tournaments);
        return tournaments;
    }

    @Transactional
    public void deleteTournament(Long id, User organizer) {
        var tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);
        scheduleService.deleteSchedules(tournament.getId(), organizer);
        formatService.deleteAllStructures(tournament.getId(), organizer);
        teamService.deleteTeams(tournament.getId(), organizer);
        tournamentRepository.delete(tournament);
    }

    @Transactional(readOnly = true)
    public List<User> getReferees(Long tournamentId, User organizer) {
        var tournament = tournamentRepository.findWithRefereesById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);
        return new ArrayList<>(tournament.getReferees());
    }

    @Transactional
    public List<User> addReferee(Long tournamentId, String refereeEmail, User organizer) {
        var tournament = tournamentRepository.findWithRefereesById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);

        var referee = userRepository.findByEmail(refereeEmail)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (!tournament.getReferees().contains(referee)) {
            tournament.getReferees().add(referee);
            tournamentRepository.save(tournament);
        }
        return tournament.getReferees();
    }

    @Transactional
    public void removeReferee(Long tournamentId, Long refereeId, User organizer) {
        var tournament = tournamentRepository.findWithRefereesById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);

        var referee = userRepository.findById(refereeId)
                .orElseThrow(() -> new NoSuchElementException("Referee not found"));
        if (!tournament.getReferees().remove(referee)) {
            throw new IllegalArgumentException("Referee not assigned");
        }
        tournamentRepository.save(tournament);
    }

    @Transactional(readOnly = true)
    public List<Tournament> getPublicTournaments() {
        var tournaments = tournamentRepository.findByPublicVisibleTrueOrderByStartDateAsc();
        refreshStatuses(tournaments);
        return tournaments;
    }

    @Transactional(readOnly = true)
    public Tournament getTournamentForPublic(Long id, String accessCode) {
        var tournament = tournamentRepository.findWithRefereesById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        var normalizedCode = Optional.ofNullable(accessCode)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);
        if (normalizedCode == null) {
            authorizationService.ensureCanViewTournament(tournament, null);
        } else if (!Objects.equals(tournament.getCode(), normalizedCode)) {
            throw new AccessDeniedException("Invalid tournament code");
        }

        refreshStatus(tournament);
        return tournament;
    }

    @Transactional
    public void followTournament(Long tournamentId, User user) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (user == null) {
            throw new AccessDeniedException("Lack of authorization");
        }
        tournament.getFollowers().add(user);
        tournamentRepository.save(tournament);
    }

    @Transactional
    public void unfollowTournament(Long tournamentId, User user) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (user == null) {
            throw new AccessDeniedException("Lack of authorization");
        }
        tournament.getFollowers().removeIf(u -> Objects.equals(u.getId(), user.getId()));
        tournamentRepository.save(tournament);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(Tournament tournament, User user) {
        if (tournament == null || user == null) {
            return false;
        }
        return tournament.getFollowers().stream()
                .anyMatch(follower -> Objects.equals(follower.getId(), user.getId()));
    }

    @Transactional
    public byte[] generateTournamentQrCode(Long tournamentId, User organizer, String baseUrl) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);

        if (!tournament.isQrCodeGenerated() || tournament.getQrCodeImage() == null) {
            var qrContent = buildTournamentUrl(baseUrl, tournament.getId());
            var qrImage = qrCodeService.generateQrCode(qrContent, 512, 512);
            tournament.setQrCodeImage(qrImage);
            tournament.setQrCodeGenerated(true);
            tournamentRepository.save(tournament);
            return qrImage;
        }

        return tournament.getQrCodeImage();
    }

    @Transactional(readOnly = true)
    public byte[] getTournamentQrCode(Long tournamentId, User organizer) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        if (!tournament.isQrCodeGenerated() || tournament.getQrCodeImage() == null) {
            throw new NoSuchElementException("QR code not generated");
        }

        return tournament.getQrCodeImage();
    }

    private String buildTournamentUrl(String baseUrl, Long tournamentId) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Base URL for QR code generation cannot be empty");
        }
        var normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return normalizedBase + "/tournaments/" + tournamentId;
    }

    private void refreshStatuses(List<Tournament> tournaments) {
        var today = LocalDate.now();
        tournaments.forEach(t -> refreshStatus(t, today));
    }

    private void refreshStatus(Tournament tournament) {
        refreshStatus(tournament, LocalDate.now());
    }

    private void refreshStatus(Tournament tournament, LocalDate today) {
        if (tournament.getStartDate() == null || tournament.getEndDate() == null) {
            return;
        }
        var status =
                tournament.getEndDate().isBefore(today) ? TournamentStatus.FINISHED :
                        (!tournament.getStartDate().isAfter(today) && !tournament.getEndDate().isBefore(today)) ? TournamentStatus.ONGOING :
                                TournamentStatus.UPCOMING;
        tournament.setStatus(status);
    }

    private String generateUniqueTournamentCode() {
        String code;
        do {
            code = randomCode();
        } while (tournamentRepository.existsByCode(code));
        return code;
    }

    private String randomCode() {
        var builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(CODE_CHARACTERS.length());
            builder.append(CODE_CHARACTERS.charAt(index));
        }
        return builder.toString();
    }
}
