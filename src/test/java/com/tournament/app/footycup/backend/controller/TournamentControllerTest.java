//package com.tournament.app.footycup.backend.controller;
//
//import com.tournament.app.footycup.backend.model.Tournament;
//import com.tournament.app.footycup.backend.model.User;
//import com.tournament.app.footycup.backend.security.filter.TokenAuthenticationFilter;
//import com.tournament.app.footycup.backend.service.TournamentService;
//import com.tournament.app.footycup.backend.service.UserService;
//import com.tournament.app.footycup.backend.config.TestSecurityConfig;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.NoSuchElementException;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
//
//
//@WebMvcTest(controllers = TournamentController.class,
//        excludeFilters = @ComponentScan.Filter(
//                type = FilterType.ASSIGNABLE_TYPE,
//                classes = TokenAuthenticationFilter.class
//        )
//)@Import(TestSecurityConfig.class)
//class TournamentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private TournamentService tournamentService;
//
//    @MockBean
//    private UserService userService;
//
//    private User mockUser;
//    private Tournament tournament;
//
//    @BeforeEach
//    void setUp() {
//        mockUser = new User();
//        mockUser.setId(1L);
//        mockUser.setEmail("user@example.com");
//
//        tournament = new Tournament();
//        tournament.setId(1L);
//        tournament.setName("Test Tournament");
//        tournament.setOrganizer(mockUser);
//        tournament.setStartDate(LocalDate.now());
//        tournament.setEndDate(LocalDate.now().plusDays(1));
//    }
//
//    @Test
//    void shouldReturnMyTournaments_whenUserAuthenticated() throws Exception {
//        when(tournamentService.getTournamentsByOrganizer(any(User.class)))
//                .thenReturn(List.of(tournament));
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
//
//        mockMvc.perform(get("/tournaments/my").with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Test Tournament"));
//    }
//
//    @Test
//    void shouldReturnNotFound_whenNoTournamentsExist() throws Exception {
//        when(tournamentService.getTournamentsByOrganizer(any(User.class)))
//                .thenThrow(new NoSuchElementException("No tournaments"));
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
//
//        mockMvc.perform(get("/tournaments/my")
//                        .with(authentication(auth)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void shouldReturnBadRequest_whenAuthenticationIsNull() throws Exception {
//        mockMvc.perform(get("/tournaments/my"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void shouldCreateTournament_whenValidRequestAndAuthenticated() throws Exception {
//        when(tournamentService.createTournament(any(Tournament.class), any(User.class)))
//                .thenReturn(tournament);
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
//
//        mockMvc.perform(post("/tournaments")
//                        .with(authentication(auth))
//                        .contentType("application/json")
//                        .content("""
//                {
//                    "name": "Test Tournament",
//                    "startDate": "2025-06-01",
//                    "endDate": "2025-06-10",
//                    "location": "Test City"
//                }
//            """))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Test Tournament"));
//    }
//
//    @Test
//    void shouldReturnBadRequest_whenCreatingTournamentWithoutAuth() throws Exception {
//        mockMvc.perform(post("/tournaments")
//                        .contentType("application/json")
//                        .content("""
//                {
//                    "name": "Test Tournament",
//                    "startDate": "2025-06-01",
//                    "endDate": "2025-06-10"
//                }
//            """))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void shouldUpdateTournament_whenValidRequestAndAuthenticated() throws Exception {
//        when(tournamentService.updateTournament(Mockito.eq(1L), any(Tournament.class), any(User.class)))
//                .thenReturn(tournament);
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
//
//        mockMvc.perform(put("/tournaments/1")
//                        .with(authentication(auth))
//                        .contentType("application/json")
//                        .content("""
//                {
//                    "name": "Updated Tournament",
//                    "startDate": "2025-06-01",
//                    "endDate": "2025-06-10"
//                }
//            """))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("Test Tournament"));
//    }
//
//    @Test
//    void shouldReturnNotFound_whenUpdatingNonexistentTournament() throws Exception {
//        when(tournamentService.updateTournament(Mockito.eq(999L), any(Tournament.class), any(User.class)))
//                .thenThrow(new NoSuchElementException("Tournament not found"));
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
//
//        mockMvc.perform(put("/tournaments/999")
//                        .with(authentication(auth))
//                        .contentType("application/json")
//                        .content("""
//                {
//                    "name": "Invalid",
//                    "startDate": "2025-06-01",
//                    "endDate": "2025-06-10"
//                }
//            """))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void shouldDeleteTournament_whenAuthenticated() throws Exception {
//        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
//
//        mockMvc.perform(delete("/tournaments/1")
//                        .with(authentication(auth)))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void shouldReturnNotFound_whenDeletingNonexistentTournament() throws Exception {
//        Mockito.doThrow(new NoSuchElementException("Not found"))
//                .when(tournamentService).deleteTournament(Mockito.eq(999L), any(User.class));
//
//        Authentication auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
//
//        mockMvc.perform(delete("/tournaments/999")
//                        .with(authentication(auth)))
//                .andExpect(status().isNotFound());
//    }
//
//}
