package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import com.tournament.app.footycup.backend.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("tournament")
public class TeamController {
    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;

    public TeamController(TeamService teamService, TeamRepository teamRepository, TournamentRepository tournamentRepository) {
        this.teamService = teamService;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @GetMapping("/{tournamentId}/teams")
    public ResponseEntity<List<Team>> getAllTeams(@PathVariable Long tournamentId) {
        List<Team> teams = teamService.getAllTeamByTournamentId(tournamentId);
        return ResponseEntity.ok(teams);
    }

//    @PostMapping("/{tournamentId}/addTeam")
//    public ResponseEntity<Object> addTeam(@PathVariable Long tournamentId, @RequestBody Team team) {
//        if(team.getName() == null || team.getCoach() == null) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Missing required fields"));
//        }
//
//        Tournament tournament = tournamentRepository.findTournamentByTournamentId(tournamentId)
//                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
//
//        team.setTournament(tournament);
//
//        Team newTeam = teamService.addTeam(team);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(newTeam);
//    }

}
