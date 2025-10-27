package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.tournament.CreateTournamentRequest;
import com.tournament.app.footycup.backend.dto.tournament.UpdateTournamentRequest;
import com.tournament.app.footycup.backend.enums.TournamentStatus;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import com.tournament.app.footycup.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final ScheduleService scheduleService;
    private final FormatService formatService;
    private final TeamService teamService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public Tournament createTournament(CreateTournamentRequest request, User organizer) {
        var tournament = new Tournament();
        tournament.setName(request.name());
        tournament.setStartDate(request.startDate());
        tournament.setEndDate(request.endDate());
        tournament.setLocation(request.location());
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

        if (!canViewTournament(tournament, requester)) {
            throw new AccessDeniedException("Lack of authorization");
        }
        refreshStatus(tournament);

        return tournament;
    }

    @Transactional
    public Tournament updateTournament(Long id, UpdateTournamentRequest updated, User organizer) {
        var tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        ensureOrganizer(tournament, organizer);
        if (updated.name() != null) tournament.setName(updated.name());
        if (updated.startDate() != null) tournament.setStartDate(updated.startDate());
        if (updated.endDate() != null) tournament.setEndDate(updated.endDate());
        if (updated.location() != null) tournament.setLocation(updated.location());
        if (updated.publicVisible() != null) tournament.setPublicVisible(updated.publicVisible());
        return tournamentRepository.save(tournament);
    }

    @Transactional(readOnly = true)
    public List<Tournament> getTournamentsByOrganizer(User organizer) {
        var tournaments = tournamentRepository.findByOrganizer(organizer);
        var today = LocalDate.now();
        tournaments.forEach(t -> {
            var s =
                    t.getEndDate().isBefore(today) ? TournamentStatus.FINISHED :
                    (!t.getStartDate().isAfter(today) && !t.getEndDate().isBefore(today)) ? TournamentStatus.ONGOING :
                    TournamentStatus.UPCOMING;
            t.setStatus(s);
        });
        return tournaments;
    }

    @Transactional
    public void deleteTournament(Long id, User organizer) {
        var tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        ensureOrganizer(tournament, organizer);
        scheduleService.deleteSchedules(tournament.getId(), organizer);
        formatService.deleteAllStructures(tournament.getId(), organizer);
        teamService.deleteTeams(tournament.getId(), organizer);
        tournamentRepository.delete(tournament);
    }

    @Transactional(readOnly = true)
    public List<User> getReferees(Long tournamentId, User organizer) {
        var tournament = tournamentRepository.findWithRefereesById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        ensureOrganizer(tournament, organizer);
        return new ArrayList<>(tournament.getReferees());
    }

    @Transactional
    public List<User> addReferee(Long tournamentId, String refereeEmail, User organizer) {
        var tournament = tournamentRepository.findWithRefereesById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        ensureOrganizer(tournament, organizer);

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
        ensureOrganizer(tournament, organizer);

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
        var today = LocalDate.now();
        tournaments.forEach(t -> refreshStatus(t, today));
        return tournaments;
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

    private boolean canViewTournament(Tournament tournament, User requester) {
        if (tournament.isPublicVisible()) {
            return true;
        }
        if (requester == null) {
            return false;
        }
        if (isOrganizer(tournament, requester)) {
            return true;
        }
        if (isReferee(tournament, requester)) {
            return true;
        }
        return teamRepository.existsByTournamentIdAndCoach_Id(tournament.getId(), requester.getId());
    }

    private boolean isOrganizer(Tournament tournament, User user) {
        return tournament.getOrganizer() != null && user != null &&
                tournament.getOrganizer().getId().equals(user.getId());
    }

    private boolean isReferee(Tournament tournament, User user) {
        if (user == null) {
            return false;
        }
        return tournament.getReferees().stream()
                .anyMatch(ref -> ref.getId().equals(user.getId()));
    }

    private void ensureOrganizer(Tournament tournament, User user) {
        if (!isOrganizer(tournament, user)) {
            throw new AccessDeniedException("Lack of authorization");
        }
    }
}
