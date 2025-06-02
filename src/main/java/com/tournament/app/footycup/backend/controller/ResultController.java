package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.BracketNode;
import com.tournament.app.footycup.backend.model.Group;
import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.FormatService;
import com.tournament.app.footycup.backend.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournament/{tournamentId}/results")
@RequiredArgsConstructor
public class ResultController {

    private final MatchService matchService;
    private final FormatService formatService;

    @Operation(summary = "Update match result")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match result updated successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized or forbidden"),
            @ApiResponse(responseCode = "404", description = "Match or tournament not found")
    })
    @PutMapping("/{matchId}")
    public ResponseEntity<Void> updateResults(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            @Parameter(description = "Match ID", required = true) @PathVariable Long matchId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated match data",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Match.class))
            )
            @RequestBody Match updated,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        matchService.updateSingleMatchResult(tournamentId, matchId, updated, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get current group standings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group standings retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Group.class)))
    })
    @GetMapping("/groups")
    public ResponseEntity<List<Group>> getGroups(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(formatService.getGroupsWithStandings(tournamentId, user));
    }

    @Operation(summary = "Get current bracket structure and results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bracket results retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BracketNode.class)))
    })
    @GetMapping("/bracket")
    public ResponseEntity<List<BracketNode>> getBracket(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(formatService.getBracketNodes(tournamentId, user));
    }

}
