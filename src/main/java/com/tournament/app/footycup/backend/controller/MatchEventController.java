package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.common.MatchEventRef;
import com.tournament.app.footycup.backend.dto.match.CreateMatchEventRequest;
import com.tournament.app.footycup.backend.mapper.CommonMapper;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.MatchEventService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/tournament/{tournamentId}/matches/{matchId}/events")
public class MatchEventController {

    private final MatchEventService matchEventService;
    private final CommonMapper commonMapper;

    @GetMapping
    public ResponseEntity<List<MatchEventRef>> getEvents(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @AuthenticationPrincipal User organizer) {
        var matches = matchEventService.getEvents(tournamentId, matchId, organizer);
        var dto = matches.stream().map(commonMapper::toMatchEventRef).toList();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<MatchEventRef> createEvent(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @RequestBody CreateMatchEventRequest request,
            @AuthenticationPrincipal User organizer) {
        var event = matchEventService.addEvent(tournamentId, matchId, request, organizer);
        return ResponseEntity.ok(commonMapper.toMatchEventRef(event));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long tournamentId,
            @PathVariable Long matchId,
            @PathVariable Long eventId,
            @AuthenticationPrincipal User organizer) {
        matchEventService.deleteEvent(tournamentId, matchId, eventId, organizer);
        return ResponseEntity.noContent().build();
    }
}