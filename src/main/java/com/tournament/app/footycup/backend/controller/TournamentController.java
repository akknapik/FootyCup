package com.tournament.app.footycup.backend.controller;
import com.tournament.app.footycup.backend.dto.UserDto;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import com.tournament.app.footycup.backend.repository.UserRepository;
import com.tournament.app.footycup.backend.service.TournamentService;
import com.tournament.app.footycup.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TournamentRepository tournamentRepository;

    public TournamentController(TournamentService tournamentService, UserRepository userRepository,
                                UserService userService, TournamentRepository tournamentRepository) {
        this.tournamentService = tournamentService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.tournamentRepository = tournamentRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<List<Tournament>> getMyTournaments(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Tournament> myTournaments = tournamentService.getTournamentsByOrganizer(user);
        return ResponseEntity.ok(myTournaments);
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody Tournament request, Authentication authentication) {
        User organizer = (User) authentication.getPrincipal();

        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setLocation(request.getLocation());
        tournament.setOrganizer(organizer);

        return ResponseEntity.ok(tournamentRepository.save(tournament));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getById(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Tournament tournament = tournamentService.getTournamentById(id, user);
        return ResponseEntity.ok(tournament);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable Long id,
                                             @RequestBody Tournament updatedData,
                                             Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Tournament updated = tournamentService.updateTournament(id, updatedData, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        tournamentService.deleteTournament(id, user);
        return ResponseEntity.noContent().build();
    }
}
