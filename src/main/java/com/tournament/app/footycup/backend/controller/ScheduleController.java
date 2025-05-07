package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Schedule;
import com.tournament.app.footycup.backend.model.ScheduleEntry;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(summary = "Get schedule for a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Schedule.class))),
            @ApiResponse(responseCode = "404", description = "Schedule not found", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Schedule> getSchedule(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Schedule schedule = scheduleService.getSchedule(tournamentId, user);
        return ResponseEntity.ok(schedule);
    }

    @Operation(summary = "Create an empty schedule starting at a specific time")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> createSchedule(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @RequestParam("start")
            @Parameter(description = "Start time of the schedule", example = "2025-05-08T14:00:00")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.createEmptySchedule(tournamentId, start, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add a break to the schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Break added successfully"),
            @ApiResponse(responseCode = "404", description = "Schedule not found", content = @Content)
    })
    @PostMapping("/{scheduleId}/break")
    public ResponseEntity<Void> addBreak(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Schedule ID") @PathVariable Long scheduleId,
            @RequestParam
            @Parameter(description = "Break duration in minutes", example = "15") int duration,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.addBreak(tournamentId, scheduleId, duration, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Recompute schedule times")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Schedule recomputed successfully"),
            @ApiResponse(responseCode = "404", description = "Schedule not found", content = @Content)
    })
    @PostMapping("/{scheduleId}/recompute")
    public ResponseEntity<Void> recompute(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Schedule ID") @PathVariable Long scheduleId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.computeSchedule(tournamentId, scheduleId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reorder entries in the schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entries reordered successfully"),
            @ApiResponse(responseCode = "404", description = "Schedule not found", content = @Content)
    })
    @PutMapping("/{scheduleId}/order")
    public ResponseEntity<Void> reorder(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Schedule ID") @PathVariable Long scheduleId,
            @RequestBody @Parameter(description = "Ordered list of schedule entry IDs") List<Long> orderedEntryIds,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.reorderEntries(tournamentId, scheduleId, orderedEntryIds, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update start time of a schedule entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entry time updated successfully"),
            @ApiResponse(responseCode = "404", description = "Entry not found", content = @Content)
    })
    @PutMapping("/{scheduleId}/{entryId}")
    public ResponseEntity<Void> updateEntryTime(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Schedule ID") @PathVariable Long scheduleId,
            @Parameter(description = "Entry ID") @PathVariable Long entryId,
            @RequestParam("start")
            @Parameter(description = "New start time", example = "2025-05-08T16:00:00")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStart,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.updateEntryTime(tournamentId, scheduleId, entryId, newStart, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add a match entry to the schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Match entry added successfully"),
            @ApiResponse(responseCode = "404", description = "Match or schedule not found", content = @Content)
    })
    @PostMapping("/{scheduleId}/entry")
    public ResponseEntity<Void> addMatchEntry(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Schedule ID") @PathVariable Long scheduleId,
            @RequestParam("matchId")
            @Parameter(description = "Match ID to add") Long matchId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.addMatch(tournamentId, scheduleId, matchId, user);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a schedule entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entry removed successfully"),
            @ApiResponse(responseCode = "404", description = "Entry not found", content = @Content)
    })
    @DeleteMapping("/{scheduleId}/entry/{entryId}")
    public ResponseEntity<Void> removeEntry(
            @Parameter(description = "Tournament ID") @PathVariable Long tournamentId,
            @Parameter(description = "Schedule ID") @PathVariable Long scheduleId,
            @Parameter(description = "Entry ID to remove") @PathVariable Long entryId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.removeEntry(tournamentId, scheduleId, entryId, user);
        return ResponseEntity.ok().build();
    }
}
