package com.tournament.app.footycup.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournament.app.footycup.backend.config.TestSecurityConfig;
import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.requests.TeamRequest;
import com.tournament.app.footycup.backend.security.filter.TokenAuthenticationFilter;
import com.tournament.app.footycup.backend.service.TeamService;
import com.tournament.app.footycup.backend.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TeamController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = TokenAuthenticationFilter.class
        )
)
@Import(TestSecurityConfig.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    @MockBean
    private TournamentService tournamentService;

    private User mockUser;
    private Team mockTeam;
    private Tournament mockTournament;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");

        mockTournament = new Tournament();
        mockTournament.setId(1L);
        mockTournament.setOrganizer(mockUser);

        mockTeam = new Team();
        mockTeam.setId(1L);
        mockTeam.setName("Team A");
        mockTeam.setTournament(mockTournament);
        mockTeam.setCoach(mockUser);

        mockPlayer = new Player();
        mockPlayer.setId(1L);
        mockPlayer.setName("Player One");
        mockPlayer.setNumber(10);
        mockPlayer.setBirthDate(LocalDate.of(2000, 1, 1));
    }

    @Test
    void shouldGetTeamById_whenAuthorized() throws Exception {
        when(teamService.getTeamById(1L, 1L, mockUser)).thenReturn(mockTeam);
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());

        mockMvc.perform(get("/tournament/1/teams/1").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Team A"));
    }

    @Test
    void shouldReturnNotFound_whenTeamDoesNotExist() throws Exception {
        when(teamService.getTeamById(1L, 999L, mockUser))
                .thenThrow(new NoSuchElementException("Team not found"));
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());

        mockMvc.perform(get("/tournament/1/teams/999").with(authentication(auth)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetTeamsByTournament_whenAuthorized() throws Exception {
        when(teamService.getTeamsByTournamentId(1L, mockUser)).thenReturn(List.of(mockTeam));
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());

        mockMvc.perform(get("/tournament/1/teams").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Team A"));
    }

    @Test
    void shouldDeleteTeam_whenAuthorized() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());

        mockMvc.perform(delete("/tournament/1/teams/1")
                        .with(authentication(auth)))
                .andExpect(status().isNoContent());
    }


    @Test
    void shouldRemovePlayer_whenAuthorized() throws Exception {
        when(teamService.removePlayer(1L, 1L, 1L, mockUser)).thenReturn(mockTeam);
        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());

        mockMvc.perform(delete("/tournament/1/teams/1/players/1")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Team A"));
    }
}
