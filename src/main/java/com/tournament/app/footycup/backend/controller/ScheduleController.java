package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Schedule;
import com.tournament.app.footycup.backend.model.ScheduleEntry;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/tournament/{tournamentId}/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "Get all schedules in a tournament")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedules retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<Schedule>> getSchedules(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Schedule> schedules = scheduleService.getSchedules(tournamentId, user);
        return ResponseEntity.ok(schedules);
    }

    @Operation(summary = "Get one schedule by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schedule retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @GetMapping("/{scheduleId}")
    public ResponseEntity<Schedule> getSchedule(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Schedule ID") @PathVariable Long scheduleId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Schedule schedule = scheduleService.getScheduleById(tournamentId, scheduleId, user);
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "Add a break to the schedule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Break added successfully"),
            @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @PostMapping("/{scheduleId}/break")
    public ResponseEntity<Void> addBreak(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @RequestParam int duration,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.addBreak(tournamentId, scheduleId, duration, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Recompute schedule times")
    @PostMapping("/{scheduleId}/recompute")
    public ResponseEntity<Void> recompute(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.computeSchedule(tournamentId, scheduleId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reorder entries in the schedule")
    @PutMapping("/{scheduleId}/order")
    public ResponseEntity<Void> reorder(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @RequestBody List<Long> orderedEntryIds,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.reorderEntries(tournamentId, scheduleId, orderedEntryIds, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update start time of a schedule entry")
    @PutMapping("/{scheduleId}/{entryId}")
    public ResponseEntity<Void> updateEntryTime(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @PathVariable Long entryId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStart,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.updateEntryTime(tournamentId, scheduleId, entryId, newStart, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add a match entry to the schedule")
    @PostMapping("/{scheduleId}/entry")
    public ResponseEntity<Void> addMatchEntry(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @RequestParam("matchId") Long matchId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.addMatch(tournamentId, scheduleId, matchId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a schedule entry")
    @DeleteMapping("/{scheduleId}/entry/{entryId}")
    public ResponseEntity<Void> removeEntry(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @PathVariable Long entryId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.removeEntry(tournamentId, scheduleId, entryId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all used match id")
    @GetMapping("/used-match")
    public ResponseEntity<List<Long>> getUsedMatchIds(
            @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Long> ids = scheduleService.getAllScheduledMatchIds(tournamentId, user);
        return ResponseEntity.ok(ids);
    }

    @Operation(summary = "Update start time of the schedule")
    @PutMapping("/{scheduleId}/start-time")
    public ResponseEntity<Void> updateScheduleStartTime(
            @PathVariable Long tournamentId,
            @PathVariable Long scheduleId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStart,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.updateScheduleStartTime(tournamentId, scheduleId, newStart, user);
        return ResponseEntity.ok().build();
    }
}
