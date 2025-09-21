package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private TournamentService tournamentService;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private FormatService formatService;

    @Mock
    private TeamService teamService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTournament_shouldSaveAndReturnTournament() {
        User organizer = new User();
        organizer.setId(1L);

        Tournament input = new Tournament();
        input.setName("Test Cup");
        input.setStartDate(LocalDate.of(2025, 6, 1));
        input.setEndDate(LocalDate.of(2025, 6, 10));
        input.setLocation("Warsaw");

        Tournament saved = new Tournament();
        saved.setId(100L);
        saved.setName("Test Cup");
        saved.setStartDate(input.getStartDate());
        saved.setEndDate(input.getEndDate());
        saved.setLocation(input.getLocation());
        saved.setOrganizer(organizer);

        when(tournamentRepository.save(any(Tournament.class))).thenReturn(saved);

        Tournament result = tournamentService.createTournament(input, organizer);

        assertNotNull(result);
        assertEquals("Test Cup", result.getName());
        assertEquals(organizer, result.getOrganizer());
        verify(tournamentRepository, times(1)).save(any(Tournament.class));
    }

    @Test
    void getTournamentById_shouldReturnTournament_whenUserIsOrganizer() {
        Long tournamentId = 1L;
        User organizer = new User();
        organizer.setId(10L);

        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setOrganizer(organizer);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        Tournament result = tournamentService.getTournamentById(tournamentId, organizer);

        assertEquals(tournament, result);
        verify(tournamentRepository).findById(tournamentId);
    }

    @Test
    void getTournamentById_shouldThrow_whenTournamentNotFound() {
        Long tournamentId = 1L;
        User user = new User();
        user.setId(10L);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->
                tournamentService.getTournamentById(tournamentId, user)
        );

        assertEquals("Tournament not found", ex.getMessage());
    }

    @Test
    void getTournamentById_shouldThrow_whenUserIsNotOrganizer() {
        Long tournamentId = 1L;
        User organizer = new User();
        organizer.setId(10L);

        User otherUser = new User();
        otherUser.setId(20L);

        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setOrganizer(organizer);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                tournamentService.getTournamentById(tournamentId, otherUser)
        );

        assertEquals("Lack of authorization", ex.getMessage());
    }

    @Test
    void updateTournament_shouldUpdateFieldsAndSave() {
        Long tournamentId = 1L;
        User organizer = new User();
        organizer.setId(10L);

        Tournament existing = new Tournament();
        existing.setId(tournamentId);
        existing.setName("Old Name");
        existing.setStartDate(LocalDate.of(2025, 6, 1));
        existing.setEndDate(LocalDate.of(2025, 6, 10));
        existing.setLocation("Old Location");
        existing.setOrganizer(organizer);

        Tournament updateData = new Tournament();
        updateData.setName("New Name");
        updateData.setStartDate(LocalDate.of(2025, 7, 1));
        updateData.setEndDate(LocalDate.of(2025, 7, 10));
        updateData.setLocation("New Location");

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(existing));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(i -> i.getArgument(0));

        Tournament updated = tournamentService.updateTournament(tournamentId, updateData, organizer);

        assertEquals("New Name", updated.getName());
        assertEquals(LocalDate.of(2025, 7, 1), updated.getStartDate());
        assertEquals(LocalDate.of(2025, 7, 10), updated.getEndDate());
        assertEquals("New Location", updated.getLocation());
        verify(tournamentRepository).save(existing);
    }

    @Test
    void updateTournament_shouldTrowAccessDenied_whenUserIsNotOwner() {
        Long tournamentId = 1L;

        User organizer = new User();
        organizer.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        Tournament tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setOrganizer(organizer);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        Tournament updateData = new Tournament();
        updateData.setName("Test");

        assertThrows(AccessDeniedException.class, () -> {
            tournamentService.updateTournament(tournamentId, updateData, otherUser);
        });

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void updateTournament_shouldThrowNoSuchElement_whenTournamentNotFound() {
        Long tournamentId = 99L;
        User user = new User();
        user.setId(1L);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        Tournament updateData = new Tournament();
        updateData.setName("Test");

        assertThrows(NoSuchElementException.class, () -> {
            tournamentService.updateTournament(tournamentId, updateData, user);
        });

        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void getTournamentsByOrganizer_shouldReturnList() {
        User organizer = new User();
        organizer.setId(1L);

        Tournament t1 = new Tournament();
        t1.setId(1L);
        t1.setName("Turniej 1");
        t1.setOrganizer(organizer);
        t1.setStartDate(LocalDate.of(2025, 6, 1));
        t1.setEndDate(LocalDate.of(2025, 6, 10));

        Tournament t2 = new Tournament();
        t2.setId(2L);
        t2.setName("Turniej 2");
        t2.setOrganizer(organizer);
        t2.setStartDate(LocalDate.of(2025, 7, 1));
        t2.setEndDate(LocalDate.of(2025, 7, 10));

        List<Tournament> tournaments = List.of(t1, t2);

        when(tournamentRepository.findByOrganizer(organizer)).thenReturn(tournaments);

        List<Tournament> result = tournamentService.getTournamentsByOrganizer(organizer);

        assertEquals(2, result.size());
        assertEquals("Turniej 1", result.get(0).getName());
        assertEquals("Turniej 2", result.get(1).getName());
    }

    @Test
    void deleteTournament_shouldDeleteIfAuthorized() {
        Long id = 1L;
        User organizer = new User();
        organizer.setId(1L);

        Tournament tournament = new Tournament();
        tournament.setId(id);
        tournament.setOrganizer(organizer);

        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

        tournamentService.deleteTournament(id, organizer);

        verify(tournamentRepository).delete(tournament);
    }

    @Test
    void deleteTournament_shouldThrowAccessDeniedIfNotOwner() {
        Long id = 1L;

        User organizer = new User();
        organizer.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);

        Tournament tournament = new Tournament();
        tournament.setId(id);
        tournament.setOrganizer(organizer);

        when(tournamentRepository.findById(id)).thenReturn(Optional.of(tournament));

        assertThrows(AccessDeniedException.class, () -> {
            tournamentService.deleteTournament(id, anotherUser);
        });

        verify(tournamentRepository, never()).delete(any(Tournament.class));
    }


}
