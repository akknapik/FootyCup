package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.UserDto;
import com.tournament.app.footycup.backend.dto.tournament.CreateTournamentRequest;
import com.tournament.app.footycup.backend.dto.tournament.UpdateTournamentRequest;
import com.tournament.app.footycup.backend.enums.TournamentStatus;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
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

    @Transactional
    public Tournament createTournament(CreateTournamentRequest request, User organizer) {
        var tournament = new Tournament();
        tournament.setName(request.name());
        tournament.setStartDate(request.startDate());
        tournament.setEndDate(request.endDate());
        tournament.setLocation(request.location());
        tournament.setOrganizer(organizer);
        var saved = tournamentRepository.save(tournament);
        scheduleService.createEmptySchedules(saved.getId(), organizer);
        return saved;
    }

    @Transactional(readOnly = true)
    public Tournament getTournamentById(Long id, User organizer) {
        var tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        if (!tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        return tournament;
    }

    @Transactional
    public Tournament updateTournament(Long id, UpdateTournamentRequest updated, User organizer) {
        var tournament = getTournamentById(id, organizer);
        if (updated.name() != null) tournament.setName(updated.name());
        if (updated.startDate() != null) tournament.setStartDate(updated.startDate());
        if (updated.endDate() != null) tournament.setEndDate(updated.endDate());
        if (updated.location() != null) tournament.setLocation(updated.location());
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
        var tournament = getTournamentById(id, organizer);
        scheduleService.deleteSchedules(tournament.getId(), organizer);
        formatService.deleteAllStructures(tournament.getId(), organizer);
        teamService.deleteTeams(tournament.getId(), organizer);
        tournamentRepository.delete(tournament);
    }

    @Transactional(readOnly = true)
    public List<User> getReferees(Long tournamentId, User organizer) {
        var tournament = tournamentRepository.findWithRefereesById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }
        return new ArrayList<>(tournament.getReferees());
    }

    @Transactional
    public List<User> addReferee(Long tournamentId, String refereeEmail, User organizer) {
        var tournament = tournamentRepository.findWithRefereesById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }
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
        if(!tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }
        var referee = userRepository.findById(refereeId)
                .orElseThrow(() -> new NoSuchElementException("Referee not found"));
        if (!tournament.getReferees().remove(referee)) {
            throw new IllegalArgumentException("Referee not assigned");
        }
        tournamentRepository.save(tournament);
    }
}
