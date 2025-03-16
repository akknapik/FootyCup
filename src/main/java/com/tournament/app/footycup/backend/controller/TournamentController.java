package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import com.tournament.app.footycup.backend.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;
    private final TournamentRepository tournamentRepository;

    public TournamentController(TournamentService tournamentService, TournamentRepository tournamentRepository) {
        this.tournamentService = tournamentService;
        this.tournamentRepository = tournamentRepository;
    }

    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTournamentById(@PathVariable("id") Long id) {
        Tournament tournament = tournamentService.getTournamentById(id);

        if(tournament==null) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Tournament not found"));
        }

        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/addTournament")
    public ResponseEntity<Object> addTournament(@RequestBody Tournament tournament) {
        Tournament newTournament = tournamentService.addTournament(tournament);
        return new ResponseEntity<>(newTournament, HttpStatus.CREATED);
    }

    @PutMapping("/updateTournament")
    public ResponseEntity<?> updateTournament(@RequestBody Tournament tournament) {
        try {
            Tournament updatedTournament = tournamentService.updateTournament(tournament);
            return ResponseEntity.ok(updatedTournament);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tournament not found"));
        }
    }

    @DeleteMapping("/deleteTournament/{id}")
    public ResponseEntity<?> deleteTournament(@PathVariable("id") Long id) {
        try {
            tournamentService.deleteTournament(id);
            return ResponseEntity.ok(Map.of("message", "Tournament deleted successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tournament not found"));
        }
    }
}
