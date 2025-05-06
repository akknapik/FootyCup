package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Schedule;
import com.tournament.app.footycup.backend.model.ScheduleEntry;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.ScheduleService;
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

    @GetMapping
    public ResponseEntity<Schedule> getSchedule(@PathVariable Long tournamentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Schedule schedule = scheduleService.getSchedule(tournamentId, user);
        return ResponseEntity.ok(schedule);
    }

    @PostMapping
    public ResponseEntity<Void> createSchedule(@PathVariable Long tournamentId,
                                               @RequestParam("start") @DateTimeFormat(iso =
                                                       DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Schedule schedule = scheduleService.createEmptySchedule(tournamentId, start, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/break")
    public ResponseEntity<Void> addBreak(@PathVariable Long tournamentId,
                                         @PathVariable Long scheduleId,
                                         @RequestParam int duration,
                                         Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ScheduleEntry scheduleEntry = scheduleService.addBreak(tournamentId, scheduleId, duration, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/recompute")
    public ResponseEntity<Void> recompute(@PathVariable Long tournamentId,
                                          @PathVariable Long scheduleId,
                                          Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.computeSchedule(tournamentId, scheduleId, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{scheduleId}/order")
    public ResponseEntity<Void> reorder(@PathVariable Long tournamentId,
                                        @PathVariable Long scheduleId,
                                        @RequestBody List<Long> orderedEntryIds,
                                        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.reorderEntries(tournamentId, scheduleId, orderedEntryIds, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{scheduleId}/{entryId}")
    public ResponseEntity<Void> updateEntryTime(@PathVariable Long tournamentId,
                                                @PathVariable Long scheduleId,
                                                @PathVariable Long entryId,
                                                @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                    LocalDateTime newStart,
                                                Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.updateEntryTime(tournamentId, scheduleId, entryId, newStart, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/entry")
    public ResponseEntity<Void> addMatchEntry(@PathVariable Long tournamentId,
                                              @PathVariable Long scheduleId,
                                              @RequestParam("matchId") Long matchId,
                                              Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        scheduleService.addMatch(tournamentId, scheduleId, matchId, user);
        return ResponseEntity.ok().build();
    }

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
}
