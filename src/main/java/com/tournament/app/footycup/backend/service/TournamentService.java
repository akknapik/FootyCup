package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.TournamentDto;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

    public List<TournamentDto> getAllTournaments() {
        List<Tournament> tournaments = tournamentRepository.findAll();
        return tournaments.stream().map(TournamentDto::new).collect(Collectors.toList());
    }

    public TournamentDto getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());

        return new TournamentDto(tournament);
    }

    public List<TournamentDto> getTournamentsByOrganizer(Long organizerId) {
        List<Tournament> tournaments = tournamentRepository.findAllByOrganizerId(organizerId);
        return tournaments.stream().map(TournamentDto::new).collect(Collectors.toList());
    }

    public TournamentDto updateTournament(Tournament tournament) {
        Tournament existingTournament = tournamentRepository.findById(tournament.getId())
                .orElseThrow(() -> new NoSuchElementException());

        if (tournament.getName() != null) existingTournament.setName(tournament.getName());
        if (tournament.getStartDate() != null) existingTournament.setStartDate(tournament.getStartDate());
        if (tournament.getEndDate() != null) existingTournament.setStartDate(tournament.getEndDate());
        if (tournament.getStatus() != null) existingTournament.setStatus(tournament.getStatus());
        existingTournament.setUpdatedAt(LocalDateTime.now());

        return new TournamentDto(tournamentRepository.save(existingTournament));
    }

    public void deleteTournament(Long id) {
        if (!tournamentRepository.existsById(id)) {
            throw new NoSuchElementException();
        }

        tournamentRepository.deleteById(id);
    }
}
