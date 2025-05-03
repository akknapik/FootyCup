package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.requests.TeamRequest;
import com.tournament.app.footycup.backend.service.TeamService;
import com.tournament.app.footycup.backend.service.TournamentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("tournament/{tournamentId}/teams")
public class TeamController {
    private final TeamService teamService;
    private final TournamentService tournamentService;

    public TeamController(TeamService teamService, TournamentService tournamentService) {
        this.teamService = teamService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public ResponseEntity<List<Team>> getTeams(@PathVariable Long tournamentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Team> teams = teamService.getTeamsByTournamentId(tournamentId, user);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long tournamentId, @PathVariable  Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team teams = teamService.getTeamById(tournamentId, id, user);
        return ResponseEntity.ok(teams);
    }

    @PostMapping
    public ResponseEntity<Team> createTeam(@PathVariable Long tournamentId, @RequestBody TeamRequest request,
                                           Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Tournament tournament = tournamentService.getTournamentById(tournamentId, user);
        Team team = teamService.createTeam(tournament.getId(), request, user);
        return ResponseEntity.ok(team);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Long tournamentId, @PathVariable Long id,
                                           @RequestBody TeamRequest updatedData, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team updated = teamService.updateTeam(tournamentId, id, updatedData, user);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long tournamentId, @PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        teamService.deleteTeam(tournamentId, id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<Team> addPlayer(@PathVariable Long tournamentId, @PathVariable Long id, @RequestBody Player request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team updated = teamService.addPlayer(tournamentId, id, request, user);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/players/{playerId}")
    public ResponseEntity<Team> updatePlayer(@PathVariable Long tournamentId, @PathVariable Long id,
                                             @PathVariable Long playerId, @RequestBody Player updatedData,
                                             Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team updated = teamService.updatePlayer(tournamentId, id, playerId, updatedData, user);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/players/{playerId}")
    public ResponseEntity<Team> removePlayer(@PathVariable Long tournamentId, @PathVariable Long id,
                                             @PathVariable Long playerId, Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    Team updated = teamService.removePlayer(tournamentId, id, playerId, user);
    return ResponseEntity.ok(updated);
    }
}
