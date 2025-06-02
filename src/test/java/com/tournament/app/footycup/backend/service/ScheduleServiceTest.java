package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.enums.EntryType;
import com.tournament.app.footycup.backend.enums.MatchStatus;
import com.tournament.app.footycup.backend.model.*;
import com.tournament.app.footycup.backend.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    @Mock private ScheduleRepository scheduleRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private ScheduleEntryRepository scheduleEntryRepository;
    @Mock private TournamentRepository tournamentRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private User organizer;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        organizer = new User();
        organizer.setId(1L);

        tournament = new Tournament();
        tournament.setId(100L);
        tournament.setOrganizer(organizer);
        tournament.setStartDate(LocalDate.of(2025, 6, 1));
        tournament.setEndDate(LocalDate.of(2025, 6, 3));
    }

    @Test
    void createEmptySchedules_shouldSaveSchedules() {
        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));

        scheduleService.createEmptySchedules(tournament.getId(), organizer);

        ArgumentCaptor<List<Schedule>> captor = ArgumentCaptor.forClass(List.class);
        verify(scheduleRepository).saveAll(captor.capture());
        List<Schedule> savedSchedules = captor.getValue();

        assertEquals(3, savedSchedules.size());
        assertEquals(LocalDateTime.of(2025, 6, 1, 0, 0), savedSchedules.get(0).getStartDateTime());
        assertEquals(tournament, savedSchedules.get(0).getTournament());
    }

    @Test
    void getSchedules_shouldReturnSchedulesForTournament() {
        List<Schedule> mockSchedules = List.of(new Schedule(), new Schedule());
        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(scheduleRepository.findByTournamentId(tournament.getId())).thenReturn(mockSchedules);

        List<Schedule> result = scheduleService.getSchedules(tournament.getId(), organizer);

        assertEquals(2, result.size());
        verify(scheduleRepository).findByTournamentId(tournament.getId());
    }

    @Test
    void addMatch_shouldReturnSavedEntry() {
        Schedule schedule = new Schedule();
        schedule.setId(200L);
        schedule.setStartDateTime(LocalDateTime.of(2025, 6, 1, 8, 0));
        schedule.setBreakBetweenMatchesInMin(5);

        Match match = new Match();
        match.setId(300L);
        match.setDurationInMin(90);

        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
        when(matchRepository.findById(match.getId())).thenReturn(Optional.of(match));

        ScheduleEntry expectedEntry = new ScheduleEntry();
        expectedEntry.setId(1L);
        expectedEntry.setSchedule(schedule);
        expectedEntry.setMatch(match);
        expectedEntry.setType(EntryType.MATCH);
        expectedEntry.setStartDateTime(schedule.getStartDateTime());

        when(scheduleEntryRepository.save(any(ScheduleEntry.class))).thenReturn(expectedEntry);

        ScheduleEntry result = scheduleService.addMatch(tournament.getId(), schedule.getId(), match.getId(), organizer);

        assertNotNull(result);
        assertEquals(EntryType.MATCH, result.getType());
        assertEquals(MatchStatus.SCHEDULED, match.getStatus());
        verify(scheduleEntryRepository).save(any(ScheduleEntry.class));
    }

    @Test
    void removeEntry_shouldUpdateScheduleAndRecompute() {
        Schedule schedule = new Schedule();
        schedule.setId(200L);
        schedule.setStartDateTime(LocalDateTime.of(2025, 6, 1, 10, 0));
        schedule.setEntries(new ArrayList<>());

        Match match = new Match();
        match.setId(300L);

        ScheduleEntry entry = new ScheduleEntry();
        entry.setId(1L);
        entry.setType(EntryType.MATCH);
        entry.setMatch(match);
        schedule.getEntries().add(entry);

        when(tournamentRepository.findById(tournament.getId())).thenReturn(Optional.of(tournament));
        when(scheduleRepository.findById(schedule.getId())).thenReturn(Optional.of(schedule));
        when(scheduleEntryRepository.findById(entry.getId())).thenReturn(Optional.of(entry));

        scheduleService.removeEntry(tournament.getId(), schedule.getId(), entry.getId(), organizer);

        verify(scheduleRepository).save(schedule);
        assertEquals(MatchStatus.NOT_SCHEDULED, match.getStatus());
        assertTrue(schedule.getEntries().isEmpty());
    }
}
