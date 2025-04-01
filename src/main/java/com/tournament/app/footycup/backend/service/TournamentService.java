package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Tournament addTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());
    }

    public List<Tournament> getTournamentsByOrganizer(Long organizerId) {
        return tournamentRepository.findAllByOrganizerId(organizerId);
    }

    public Tournament updateTournament(Tournament tournament) {
        Tournament existingTournament = tournamentRepository.findById(tournament.getId())
                .orElseThrow(() -> new NoSuchElementException());

        if (tournament.getName() != null) existingTournament.setName(tournament.getName());
        if (tournament.getStartDate() != null) existingTournament.setStartDate(tournament.getStartDate());
        if (tournament.getEndDate() != null) existingTournament.setEndDate(tournament.getEndDate());
        if (tournament.getLocation() != null) existingTournament.setLocation(tournament.getLocation());
        if (tournament.getStatus() != null) existingTournament.setStatus(tournament.getStatus());
        if (tournament.getSystem() != null) existingTournament.setSystem(tournament.getSystem());

        return tournamentRepository.save(existingTournament);
    }

    public void deleteTournament(Long id) {
        if (!tournamentRepository.existsById(id)) {
            throw new NoSuchElementException();
        }

        tournamentRepository.deleteById(id);
    }
}
