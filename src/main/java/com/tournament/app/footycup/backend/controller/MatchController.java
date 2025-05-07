package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/tournament/{tournamentId}/matches")
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "Get all matches in a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matches retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Match.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Match>> getMatches(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Match> matches = matchService.getMatches(tournamentId, user);
        return ResponseEntity.ok(matches);
    }

    @Operation(summary = "Generate group stage matches for a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group matches generated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot generate matches due to conflicts or invalid state", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> generateGroupMatches(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        matchService.generateGroupMatches(tournamentId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete all matches in a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All matches deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllMatches(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        matchService.deleteAllMatches(tournamentId, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a specific match from a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Match deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Match not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Match ID") @PathVariable Long matchId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        matchService.deleteMatch(tournamentId, matchId, user);
        return ResponseEntity.noContent().build();
    }
}
