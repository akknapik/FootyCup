package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.team.*;
import com.tournament.app.footycup.backend.mapper.TeamMapper;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.TeamService;
import com.tournament.app.footycup.backend.service.TeamStatisticsService;
import com.tournament.app.footycup.backend.service.TournamentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("tournament/{tournamentId}/teams")
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final TeamStatisticsService teamStatisticsService;


    @GetMapping
    public ResponseEntity<List<TeamItemResponse>> getTeams(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        var teams = teamService.getTeamsByTournamentId(tournamentId, organizer);
        var dto = teams.stream().map(teamMapper::toItem).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(
            @PathVariable Long tournamentId,
            @PathVariable Long id,
            @AuthenticationPrincipal User organizer) {
        var team = teamService.getTeamById(tournamentId, id, organizer);
        return ResponseEntity.ok(teamMapper.toResponse(team));
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @PathVariable Long tournamentId,
            @RequestBody @Valid CreateTeamRequest request,
            @AuthenticationPrincipal User organizer) {
        var team = teamService.createTeam(tournamentId, request, organizer);
        var body = teamMapper.toResponse(team);
        var location =
                UriComponentsBuilder.fromPath("tournament/{tournamentId}/teams").buildAndExpand(team.getId()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long tournamentId,
            @PathVariable Long id,
            @RequestBody @Valid UpdateTeamRequest updatedData,
            @AuthenticationPrincipal User organizer) {
        var updated = teamService.updateTeam(tournamentId, id, updatedData, organizer);
        return ResponseEntity.ok(teamMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable Long tournamentId,
            @PathVariable Long id,
            @AuthenticationPrincipal User organizer) {
        teamService.deleteTeam(tournamentId, id, organizer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/players/{playerId}/statistics")
    public ResponseEntity<PlayerStatisticsResponse> getPlayerStatistics(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            @AuthenticationPrincipal User organizer) {
        var statistics = teamStatisticsService.getPlayerStatistics(tournamentId, teamId, playerId, organizer);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{teamId}/statistics")
    public ResponseEntity<TeamStatisticsResponse> getTeamStatistics(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId,
            @AuthenticationPrincipal User organizer) {
        var statistics = teamStatisticsService.getTeamStatistics(tournamentId, teamId, organizer);
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<TeamResponse> addPlayer(
            @PathVariable Long tournamentId,
            @PathVariable Long id,
            @RequestBody @Valid CreatePlayerRequest request,
            @AuthenticationPrincipal User organizer) {
        var updated = teamService.addPlayer(tournamentId, id, request, organizer);
        var dto = teamMapper.toResponse(updated);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/players/{playerId}")
    public ResponseEntity<TeamResponse> updatePlayer(
            @PathVariable Long tournamentId,
            @PathVariable Long id,
            @PathVariable Long playerId,
            @RequestBody UpdatePlayerRequest updatedData,
            @AuthenticationPrincipal User organizer) {
        var updated = teamService.updatePlayer(tournamentId, id, playerId, updatedData, organizer);
        var dto = teamMapper.toResponse(updated);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/players/{playerId}")
    public ResponseEntity<TeamResponse> removePlayer(
            @PathVariable Long tournamentId,
            @PathVariable Long id,
            @PathVariable Long playerId,
            @AuthenticationPrincipal User organizer) {
        var updated = teamService.removePlayer(tournamentId, id, playerId, organizer);
        var dto = teamMapper.toResponse(updated);
        return ResponseEntity.ok(dto);
    }
}
