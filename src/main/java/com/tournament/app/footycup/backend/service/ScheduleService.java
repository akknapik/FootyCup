package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.enums.EntryType;
import com.tournament.app.footycup.backend.enums.MatchStatus;
import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.Schedule;
import com.tournament.app.footycup.backend.model.ScheduleEntry;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import com.tournament.app.footycup.backend.repository.ScheduleEntryRepository;
import com.tournament.app.footycup.backend.repository.ScheduleRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MatchRepository matchRepository;
    private final ScheduleEntryRepository scheduleEntryRepository;
    private final TournamentRepository tournamentRepository;

    public List<Schedule> getSchedules(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!tournament.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        return scheduleRepository.findByTournamentId(tournamentId);
    }

    public Schedule getScheduleById(Long tournamentId, Long scheduleId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!tournament.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
    }

    public void createEmptySchedules(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!tournament.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        LocalDate start = tournament.getStartDate();
        LocalDate end = tournament.getEndDate();
        List<Schedule> schedules = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Schedule schedule = new Schedule();
            schedule.setTournament(tournament);
            schedule.setStartDateTime(date.atStartOfDay());
            schedules.add(schedule);
        }
        scheduleRepository.saveAll(schedules);
    }

    public void computeSchedule(Long tournamentId, Long scheduleId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!tournament.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        Schedule sched = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        LocalDateTime cursor = sched.getStartDateTime();
        for (ScheduleEntry e : sched.getEntries()) {
            e.setStartDateTime(cursor);
            cursor = cursor.plusMinutes(e.getDurationInMin());
            scheduleEntryRepository.save(e);
        }
    }

    public ScheduleEntry addMatch(Long tournamentId, Long scheduleId, Long matchId, User user) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!t.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        Schedule sched = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        Match m = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
        ScheduleEntry e = new ScheduleEntry();
        e.setSchedule(sched);
        e.setType(EntryType.MATCH);
        e.setMatch(m);
        e.setDurationInMin(m.getDurationInMin());
        e.setStartDateTime(sched.getStartDateTime());
        m.setStatus(MatchStatus.SCHEDULED);
        return scheduleEntryRepository.save(e);
    }

    public void removeEntry(Long tournamentId, Long scheduleId, Long entryId, User user) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!t.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        Schedule sched = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        ScheduleEntry entry = scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Entry not found"));
        if(entry.getType().equals(EntryType.MATCH)) {
            entry.getMatch().setStatus(MatchStatus.NOT_SCHEDULED);
        }
        sched.getEntries().removeIf(e -> e.getId().equals(entryId));
        scheduleRepository.save(sched);
        computeSchedule(tournamentId, scheduleId, user);
    }

    public ScheduleEntry addBreak(Long tournamentId, Long scheduleId, int durationInMin, User user) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!t.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        Schedule sched = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        ScheduleEntry e = new ScheduleEntry();
        e.setSchedule(sched);
        e.setType(EntryType.BREAK);
        e.setDurationInMin(durationInMin);
        e.setStartDateTime(sched.getStartDateTime());
        e = scheduleEntryRepository.save(e);
        computeSchedule(tournamentId, scheduleId, user);
        return e;
    }

    public Schedule reorderEntries(Long tournamentId, Long scheduleId, List<Long> orderedEntryIds, User user) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!t.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        Schedule sched = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        List<ScheduleEntry> newOrder = orderedEntryIds.stream()
                .map(id -> scheduleEntryRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Entry not found")))
                .collect(Collectors.toList());
        sched.getEntries().clear();
        sched.getEntries().addAll(newOrder);
        scheduleRepository.save(sched);
        LocalDateTime cursor = sched.getStartDateTime();
        for (ScheduleEntry e : sched.getEntries()) {
            e.setStartDateTime(cursor);
            cursor = cursor.plusMinutes(e.getDurationInMin());
            scheduleEntryRepository.save(e);
        }
        return sched;
    }

    public ScheduleEntry updateEntryTime(Long tournamentId, Long scheduleId, Long entryId, LocalDateTime newStart, User user) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!t.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        ScheduleEntry e = scheduleEntryRepository.findById(entryId)
                .orElseThrow(() -> new NoSuchElementException("Schedule entry not found"));
        e.setStartDateTime(newStart);
        return scheduleEntryRepository.save(e);
    }

    public List<Long> getAllScheduledMatchIds(Long tournamentId, User user) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!t.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();
        return scheduleEntryRepository.findAllScheduledMatchIdsByTournamentId(tournamentId);
    }

    public void updateScheduleStartTime(Long tournamentId, Long scheduleId, LocalDateTime newStart, User user) {
        Tournament t = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!t.getOrganizer().getId().equals(user.getId())) throw new IllegalArgumentException();

        Schedule s = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NoSuchElementException("Schedule not found"));
        s.setStartDateTime(newStart);
        scheduleRepository.save(s);
    }
}
