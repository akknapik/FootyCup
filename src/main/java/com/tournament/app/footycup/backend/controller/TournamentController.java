package com.tournament.app.footycup.backend.controller;
import com.tournament.app.footycup.backend.dto.TournamentDto;
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
    public ResponseEntity<List<TournamentDto>> getAllTournaments() {
        List<TournamentDto> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(tournaments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTournamentById(@PathVariable("id") Long id) {
        try {
            TournamentDto tournament = tournamentService.getTournamentById(id);
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
        List<TournamentDto> tournaments = tournamentService.getTournamentsByOrganizer(id);
        return ResponseEntity.ok(tournaments);
    }

    @PostMapping("/addTournament")
    public ResponseEntity<Object> addTournament(@RequestBody TournamentDto tournamentDto) {
        if(tournamentDto.getOrganizer() == null || tournamentDto.getName() == null || tournamentDto.getStartDate() == null || tournamentDto.getEndDate() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Missing required fields"));
        }


        User organizer = userRepository.findById(tournamentDto.getOrganizer().getId())
                .orElse(null);
        if(organizer == null) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","Organizer not found"));
        }

        Tournament tournament = new Tournament();
        tournament.setName(tournamentDto.getName());
        tournament.setStartDate(tournamentDto.getStartDate());
        tournament.setEndDate(tournamentDto.getEndDate());
        tournament.setOrganizer(organizer);

        Tournament newTournament = tournamentService.addTournament(tournament);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TournamentDto(newTournament));
    }

    @PutMapping("/updateTournament")
    public ResponseEntity<?> updateTournament(@RequestBody Tournament tournament) {
        if(tournament.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Tournament ID is required"));
        }

        try {
            TournamentDto updatedTournament = tournamentService.updateTournament(tournament);
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
