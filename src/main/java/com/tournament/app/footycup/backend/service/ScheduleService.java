package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.enums.EntryType;
import com.tournament.app.footycup.backend.model.*;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import com.tournament.app.footycup.backend.repository.ScheduleEntryRepository;
import com.tournament.app.footycup.backend.repository.ScheduleRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MatchRepository matchRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;
    private final TournamentRepository tournamentRepository;

    public Schedule getSchedule(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        Schedule schedule = scheduleRepository.findByTournamentId(tournamentId);
        return schedule;
    }

    public Schedule createEmptySchedule(Long tournamentId, LocalDateTime startDateTime, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        Schedule schedule = new Schedule();
        schedule.setTournament(tournament);
        schedule.setStartDateTime(startDateTime);
        scheduleRepository.save(schedule);

        return schedule;
    }

    public void computeSchedule(Long tournamentId, Long scheduleId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        LocalDateTime cursor = schedule.getStartDateTime();

        List<ScheduleEntry> entries = schedule.getEntries();
        for(ScheduleEntry e : entries) {
            e.setStartDateTime(cursor);
            cursor.plusMinutes(e.getDurationInMin());
            scheduleEntryRepository.save(e);
        }
    }

    public ScheduleEntry addMatch(Long tournamentId, Long scheduleId, Long matchId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));

        ScheduleEntry entry = new ScheduleEntry();
        entry.setSchedule(schedule);
        entry.setType(EntryType.MATCH);
        entry.setMatch(match);
        entry.setDurationInMin(match.getDurationInMin());
        entry = scheduleEntryRepository.save(entry);

        computeSchedule(tournamentId, scheduleId, user);
        return entry;
    }

    public void removeEntry(Long tournamentId, Long scheduleId, Long entryId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));

        ScheduleEntry entry = scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entry not found"));

        scheduleEntryRepository.delete(entry);
        computeSchedule(tournamentId, scheduleId, user);
    }

    public ScheduleEntry addBreak(Long tournamentId, Long scheduleId, int durationInMin, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        ScheduleEntry br = new ScheduleEntry();
        br.setSchedule(schedule);
        br.setType(EntryType.BREAK);
        br.setDurationInMin(durationInMin);
        scheduleEntryRepository.save(br);
        computeSchedule(tournamentId, schedule.getId(), user);
        return br;
    }

    public void reorderEntries(Long tournamentId, Long scheduleId, List<Long> orderedEntryIds, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        List<ScheduleEntry> newOrder = orderedEntryIds.stream()
                .map(scheduleEntryRepository::getOne)
                .collect(Collectors.toList());
        schedule.getEntries().clear();
        schedule.getEntries().addAll(newOrder);
        scheduleRepository.save(schedule);
        computeSchedule(tournamentId, schedule.getId(), user);
    }

    public ScheduleEntry updateEntryTime(Long tournamentId, Long scheduleId, Long entryId, LocalDateTime newStart, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));


        ScheduleEntry entry = scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Schedule entry not found"));


        entry.setStartDateTime(newStart);
        return scheduleEntryRepository.save(entry);
    }
}
