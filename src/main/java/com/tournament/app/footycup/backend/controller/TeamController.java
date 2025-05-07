package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.requests.TeamRequest;
import com.tournament.app.footycup.backend.service.TeamService;
import com.tournament.app.footycup.backend.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all teams in a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teams retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Team>> getTeams(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Team> teams = teamService.getTeamsByTournamentId(tournamentId, user);
        return ResponseEntity.ok(teams);
    }

    @Operation(summary = "Get a specific team by ID in a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Team ID") @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team teams = teamService.getTeamById(tournamentId, id, user);
        return ResponseEntity.ok(teams);
    }

    @Operation(summary = "Create a new team in a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or tournament not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Team> createTeam(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @RequestBody @Parameter(description = "Team request body") TeamRequest request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Tournament tournament = tournamentService.getTournamentById(tournamentId, user);
        Team team = teamService.createTeam(tournament.getId(), request, user);
        return ResponseEntity.ok(team);
    }

    @Operation(summary = "Update an existing team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Team ID") @PathVariable Long id,
            @RequestBody @Parameter(description = "Updated team data") TeamRequest updatedData,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team updated = teamService.updateTeam(tournamentId, id, updatedData, user);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a team from a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Team deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Team ID") @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        teamService.deleteTeam(tournamentId, id, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a player to a team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player added successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))),
            @ApiResponse(responseCode = "404", description = "Team or tournament not found", content = @Content)
    })
    @PostMapping("/{id}/players")
    public ResponseEntity<Team> addPlayer(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Team ID") @PathVariable Long id,
            @RequestBody @Parameter(description = "Player data") Player request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team updated = teamService.addPlayer(tournamentId, id, request, user);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Update a player in a team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))),
            @ApiResponse(responseCode = "404", description = "Player not found", content = @Content)
    })
    @PutMapping("/{id}/players/{playerId}")
    public ResponseEntity<Team> updatePlayer(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Team ID") @PathVariable Long id,
            @Parameter(description = "Player ID") @PathVariable Long playerId,
            @RequestBody @Parameter(description = "Updated player data") Player updatedData,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team updated = teamService.updatePlayer(tournamentId, id, playerId, updatedData, user);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Remove a player from a team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Team.class))),
            @ApiResponse(responseCode = "404", description = "Player or team not found", content = @Content)
    })
    @DeleteMapping("/{id}/players/{playerId}")
    public ResponseEntity<Team> removePlayer(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Team ID") @PathVariable Long id,
            @Parameter(description = "Player ID") @PathVariable Long playerId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Team updated = teamService.removePlayer(tournamentId, id, playerId, user);
        return ResponseEntity.ok(updated);
    }
}
