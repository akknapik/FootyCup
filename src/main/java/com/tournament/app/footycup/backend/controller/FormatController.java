package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.BracketNode;
import com.tournament.app.footycup.backend.model.Group;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.FormatService;
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
@RequestMapping("tournament/{tournamentId}/format")
public class FormatController {

    private final FormatService formatService;

    @Operation(summary = "Check if a tournament format already exists")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Format existence check completed",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    })
    @GetMapping
    public ResponseEntity<Boolean> formatExits(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean exists = formatService.structureExists(tournamentId, user);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Generate group format structure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group format created successfully")
    })
    @PostMapping("/group")
    public ResponseEntity<Void> generateGroupFormat(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            @Parameter(description = "Number of groups", required = true) @RequestParam int groupCount,
            @Parameter(description = "Number of teams per group", required = true) @RequestParam int teamsPerGroup,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.generateGroupStructure(tournamentId, groupCount, teamsPerGroup, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generate bracket format structure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bracket format created successfully")
    })
    @PostMapping("/bracket")
    public ResponseEntity<Void> generateBracketFormat(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            @Parameter(description = "Total number of teams", required = true) @RequestParam int totalTeams,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.generateBracketStructure(tournamentId, totalTeams, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Generate mixed group + bracket format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mixed format created successfully")
    })
    @PostMapping("/mixed")
    public ResponseEntity<Void> generateMixedFormat(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            @Parameter(description = "Number of groups", required = true) @RequestParam int groupCount,
            @Parameter(description = "Teams per group", required = true) @RequestParam int teamsPerGroup,
            @Parameter(description = "Advancing teams per group", required = true) @RequestParam int advancing,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.generateMixedStructure(tournamentId, groupCount, teamsPerGroup, advancing, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign a specific team to a group slot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team assigned to slot successfully")
    })
    @PutMapping("/{slotId}/assign/{teamId}")
    public ResponseEntity<Void> assignTeam(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            @Parameter(description = "Slot ID", required = true) @PathVariable Long slotId,
            @Parameter(description = "Team ID", required = true) @PathVariable Long teamId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.assignTeamToSlot(tournamentId, slotId, teamId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Randomly assign all teams to groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teams randomly assigned to groups")
    })
    @PostMapping("/assign-random")
    public ResponseEntity<Void> assignTeamsRandomly(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.assignTeamsRandomlyToGroups(tournamentId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign a team to a bracket node")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team assigned to bracket node successfully")
    })
    @PutMapping("/bracket/{nodeId}")
    public ResponseEntity<Void> assignTeamToNode(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            @Parameter(description = "Node ID", required = true) @PathVariable Long nodeId,
            @Parameter(description = "Team ID", required = true) @RequestParam Long teamId,
            @Parameter(description = "Is this the home team?", required = true) @RequestParam boolean homeTeam,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.assignTeamToNode(tournamentId, nodeId, teamId, homeTeam, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get group stage configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Groups retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = Group.class)))
    })
    @GetMapping("/groups")
    public ResponseEntity<List<Group>> getGroups(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Group> groups = formatService.getGroups(tournamentId, user);
        return ResponseEntity.ok(groups);
    }

    @Operation(summary = "Get bracket configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bracket retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = BracketNode.class)))
    })
    @GetMapping("/bracket")
    public ResponseEntity<List<BracketNode>> getBracket(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<BracketNode> bracketNodes = formatService.getBracketNodes(tournamentId, user);
        return ResponseEntity.ok(bracketNodes);
    }

    @Operation(summary = "Delete all format structures for a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All structures deleted successfully")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllStructures(
            @Parameter(description = "Tournament ID", required = true) @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.deleteAllStructures(tournamentId, user);
        return ResponseEntity.noContent().build();
    }
}
