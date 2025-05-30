package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final ScheduleService scheduleService;

    public Tournament createTournament(Tournament request, User organizer) {
        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setLocation(request.getLocation());
        tournament.setOrganizer(organizer);
        Tournament saved = tournamentRepository.save(tournament);
        scheduleService.createEmptySchedules(saved.getId(), organizer);
        return saved;
    }

    public Tournament getTournamentById(Long id, User user) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));

        if (!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        return tournament;
    }

    public Tournament updateTournament(Long id, Tournament updatedData, User user) {
        Tournament tournament = getTournamentById(id, user);

        if (updatedData.getName() != null) {
            tournament.setName(updatedData.getName());
        }
        if (updatedData.getStartDate() != null) {
            tournament.setStartDate(updatedData.getStartDate());
        }
        if (updatedData.getEndDate() != null) {
            tournament.setEndDate(updatedData.getEndDate());
        }
        if (updatedData.getLocation() != null) {
            tournament.setLocation(updatedData.getLocation());
        }

        return tournamentRepository.save(tournament);
    }

    public List<Tournament> getTournamentsByOrganizer(User organizer) {
        return tournamentRepository.findByOrganizer(organizer);
    }


    public void deleteTournament(Long id, User user) {
        Tournament tournament = getTournamentById(id, user);
        tournamentRepository.delete(tournament);
    }
}
