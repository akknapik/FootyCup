package com.tournament.app.footycup.backend.controller;
import com.tournament.app.footycup.backend.dto.UserDto;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.UserRepository;
import com.tournament.app.footycup.backend.service.TournamentService;
import com.tournament.app.footycup.backend.service.UserService;
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
    private final UserRepository userRepository;
    private final UserService userService;

    public TournamentController(TournamentService tournamentService, UserRepository userRepository,
                                UserService userService) {
        this.tournamentService = tournamentService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTournamentById(@PathVariable("id") Long id) {
        try {
            Tournament tournament = tournamentService.getTournamentById(id);
            return ResponseEntity.ok(tournament);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tournament not found"));
        }
    }

    @GetMapping("/byOrganizer/{id}")
    public ResponseEntity<?> getTournamentsByOrganizerId(@PathVariable("id") Long id) {
        UserDto existingUser = userService.getUserById(id);
        if(existingUser == null) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Organizer not found"));
        }
        List<Tournament> tournaments = tournamentService.getTournamentsByOrganizer(id);
        return ResponseEntity.ok(tournaments);
    }

    @PostMapping("/addTournament")
    public ResponseEntity<Object> addTournament(@RequestBody Tournament tournament) {
        if(tournament.getOrganizer() == null || tournament.getName() == null || tournament.getStartDate() == null || tournament.getEndDate() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Missing required fields"));
        }

        User organizer = userRepository.findById(tournament.getOrganizer().getId())
                .orElse(null);
        if(organizer == null) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Organizer not found"));
        }

        Tournament newTournament = tournamentService.addTournament(tournament);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTournament);
    }

    @PutMapping("/updateTournament")
    public ResponseEntity<?> updateTournament(@RequestBody Tournament tournament) {
        if(tournament.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Tournament ID is required"));
        }

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
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "Tournament deleted " +
                    "successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Tournament not found"));
        }
    }
}
