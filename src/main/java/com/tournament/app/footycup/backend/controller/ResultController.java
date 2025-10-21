package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.UpdateMatchResultRequest;
import com.tournament.app.footycup.backend.dto.format.bracket.BracketNodeResponse;
import com.tournament.app.footycup.backend.dto.format.group.GroupResponse;
import com.tournament.app.footycup.backend.mapper.BracketMapper;
import com.tournament.app.footycup.backend.mapper.GroupMapper;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournament/{tournamentId}/results")
@RequiredArgsConstructor
public class ResultController {

    private final MatchService matchService;
    private final FormatService formatService;
    private final GroupMapper groupMapper;
    private final BracketMapper bracketMapper;

    @PutMapping("/{matchId}")
    public ResponseEntity<Void> updateResults(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestBody @Valid UpdateMatchResultRequest updated,
            @AuthenticationPrincipal User user
            ) {
        matchService.updateSingleMatchResult(tournamentId, matchId, updated, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupResponse>> getGroups(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User user
    ) {
        var groups = formatService.getGroupsWithStandings(tournamentId, user);
        var dto = groups.stream().map(groupMapper::toResponse).toList();
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/bracket")
    public ResponseEntity<List<BracketNodeResponse>> getBracket(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User user
    ) {
        var bracketNodes = formatService.getBracketNodes(tournamentId, user);
        var dto = bracketNodes.stream().map(bracketMapper::toResponse).toList();
        return ResponseEntity.ok(dto);
    }

}
