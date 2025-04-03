package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public Tournament getTournamentById(Long id, User user) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono turnieju"));

        if (!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Brak dostępu do turnieju");
        }

        return tournament;
    }

    public Tournament updateTournament(Long id, Tournament updatedData, User user) {
        Tournament tournament = getTournamentById(id, user);

        tournament.setName(updatedData.getName());
        tournament.setStartDate(updatedData.getStartDate());
        tournament.setEndDate(updatedData.getEndDate());
        tournament.setLocation(updatedData.getLocation());

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
