package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.format.GenerateMixRequest;
import com.tournament.app.footycup.backend.dto.format.bracket.AssignTeamToNodeRequest;
import com.tournament.app.footycup.backend.dto.format.bracket.BracketNodeResponse;
import com.tournament.app.footycup.backend.dto.format.bracket.GenerateBracketRequest;
import com.tournament.app.footycup.backend.dto.format.group.*;
import com.tournament.app.footycup.backend.mapper.BracketMapper;
import com.tournament.app.footycup.backend.mapper.GroupMapper;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.FormatService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("tournament/{tournamentId}/format")
public class FormatController {

    private final FormatService formatService;
    private final GroupMapper groupMapper;
    private final BracketMapper bracketMapper;

    @GetMapping
    public ResponseEntity<Boolean> formatExits(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        return ResponseEntity.ok(formatService.structureExists(tournamentId, organizer));
    }

    @PostMapping("/group")
    public ResponseEntity<Void> generateGroupFormat(
            @PathVariable Long tournamentId,
            @RequestBody @Valid GenerateGroupRequest request,
            @AuthenticationPrincipal User organizer) {
        formatService.generateGroupStructure(tournamentId, request, organizer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bracket")
    public ResponseEntity<Void> generateBracketFormat(
            @PathVariable Long tournamentId,
            @RequestBody @Valid GenerateBracketRequest request,
            @AuthenticationPrincipal User organizer) {
        formatService.generateBracketStructure(tournamentId, request, organizer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mixed")
    public ResponseEntity<Void> generateMixedFormat(
            @PathVariable Long tournamentId,
            @RequestBody @Valid GenerateMixRequest request,
            @AuthenticationPrincipal User organizer) {
        formatService.generateMixedStructure(tournamentId, request, organizer);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/slot")
    public ResponseEntity<Void> assignTeam(
            @PathVariable Long tournamentId,
            @RequestBody @Valid AssignTeamToSlotRequest request,
            @AuthenticationPrincipal User organizer) {
        formatService.assignTeamToSlot(tournamentId, request,organizer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-random")
    public ResponseEntity<Void> assignTeamsRandomly(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        formatService.assignTeamsRandomlyToGroups(tournamentId, organizer);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/bracket")
    public ResponseEntity<Void> assignTeamToNode(
            @PathVariable Long tournamentId,
            @RequestBody @Valid AssignTeamToNodeRequest request,
            @AuthenticationPrincipal User organizer) {
        formatService.assignTeamToNode(tournamentId, request, organizer);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupResponse>> getGroups(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        var groups = formatService.getGroups(tournamentId, organizer);
        var dto = groups.stream().map(groupMapper::toResponse).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long tournamentId,
                                                  @PathVariable Long groupId,
                                                  @AuthenticationPrincipal User organizer) {
        var group = formatService.getGroup(tournamentId, groupId, organizer);
        return ResponseEntity.ok(groupMapper.toResponse(group));
    }

    @GetMapping("/bracket")
    public ResponseEntity<List<BracketNodeResponse>> getBracket(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        var bracketNodes = formatService.getBracketNodes(tournamentId, organizer);
        var dto = bracketNodes.stream().map(bracketMapper::toResponse).toList();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllStructures(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        formatService.deleteAllStructures(tournamentId, organizer);
        return ResponseEntity.noContent().build();
    }
}
