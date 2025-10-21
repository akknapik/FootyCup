package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.schedule.*;
import com.tournament.app.footycup.backend.mapper.ScheduleMapper;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/tournament/{tournamentId}/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleMapper scheduleMapper;

    @GetMapping
    public ResponseEntity<List<ScheduleListItemResponse>> getSchedules(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        var schedules = scheduleService.getSchedules(tournamentId, organizer);
        var dto = schedules.stream().map(scheduleMapper::toListItem).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User organizer) {
        var schedule = scheduleService.getScheduleById(tournamentId, scheduleId, organizer);
        return ResponseEntity.ok(scheduleMapper.toResponse(schedule));
    }

    @PostMapping("/{scheduleId}/break")
    public ResponseEntity<Void> addBreak(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @RequestBody @Valid AddBreakRequest request,
            @AuthenticationPrincipal User organizer) {
        scheduleService.addBreak(tournamentId, scheduleId, request, organizer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/recompute")
    public ResponseEntity<Void> recompute(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User organizer) {
        scheduleService.computeSchedule(tournamentId, scheduleId, organizer);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{scheduleId}/order")
    public ResponseEntity<Void> reorder(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @RequestBody @Valid ReorderEntriesRequest request,
            @AuthenticationPrincipal User organizer) {
        scheduleService.reorderEntries(tournamentId, scheduleId, request, organizer);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{scheduleId}/{entryId}")
    public ResponseEntity<Void> updateEntryTime(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @PathVariable Long entryId,
            @RequestBody @Valid UpdateEntryTimeRequest request,
            @AuthenticationPrincipal User organizer) {
        scheduleService.updateEntryTime(tournamentId, scheduleId, entryId, request, organizer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/entry")
    public ResponseEntity<Void> addMatchEntry(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @RequestBody @Valid AddMatchEntryRequest request,
            @AuthenticationPrincipal User organizer) {
        scheduleService.addMatch(tournamentId, scheduleId, request, organizer);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{scheduleId}/entry/{entryId}")
    public ResponseEntity<Void> removeEntry(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @PathVariable Long entryId,
            @AuthenticationPrincipal User organizer) {
        scheduleService.removeEntry(tournamentId, scheduleId, entryId, organizer);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/used-match")
    public ResponseEntity<List<Long>> getUsedMatchIds(
            @PathVariable Long tournamentId,
            @AuthenticationPrincipal User organizer) {
        List<Long> ids = scheduleService.getAllScheduledMatchIds(tournamentId, organizer);
        return ResponseEntity.ok(ids);
    }

    @PutMapping("/{scheduleId}/start-time")
    public ResponseEntity<Void> updateScheduleStartTime(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @RequestBody @Valid UpdateScheduleStartTimeRequest request,
            @AuthenticationPrincipal User organizer) {
        scheduleService.updateScheduleStartTime(tournamentId, scheduleId, request, organizer);
        return ResponseEntity.ok().build();
    }
}
