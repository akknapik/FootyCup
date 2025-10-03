//package com.tournament.app.footycup.backend.service;
//
//import com.tournament.app.footycup.backend.model.Player;
//import com.tournament.app.footycup.backend.model.Team;
//import com.tournament.app.footycup.backend.model.Tournament;
//import com.tournament.app.footycup.backend.model.User;
//import com.tournament.app.footycup.backend.repository.PlayerRepository;
//import com.tournament.app.footycup.backend.repository.TeamRepository;
//import com.tournament.app.footycup.backend.repository.TournamentRepository;
//import com.tournament.app.footycup.backend.repository.UserRepository;
//import com.tournament.app.footycup.backend.requests.TeamRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class TeamServiceTest {
//
//    @Mock
//    private PlayerRepository playerRepository;
//
//    @Mock
//    private TeamRepository teamRepository;
//
//    @Mock
//    private TournamentRepository tournamentRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private TeamService teamService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void getTeamById_shouldReturnTeamIfAuthorized() {
//        User user = new User();
//        user.setId(1L);
//        Tournament tournament = new Tournament();
//        tournament.setId(10L);
//        tournament.setOrganizer(user);
//        Team team = new Team();
//        team.setId(100L);
//        team.setTournament(tournament);
//        team.setCoach(user);
//
//        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
//
//        Team result = teamService.getTeamById(10L, 100L, user);
//        assertEquals(100L, result.getId());
//    }
//
//    @Test
//    void getTeamsByTournamentId_shouldReturnListIfAuthorized() {
//        User user = new User();
//        user.setId(1L);
//        Tournament tournament = new Tournament();
//        tournament.setId(10L);
//        tournament.setOrganizer(user);
//        Team team = new Team();
//        team.setId(100L);
//
//        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
//        when(teamRepository.findByTournamentId(10L)).thenReturn(List.of(team));
//
//        List<Team> teams = teamService.getTeamsByTournamentId(10L, user);
//        assertEquals(1, teams.size());
//    }
//
//    @Test
//    void createTeam_savesAndReturnsTeam() {
//        User organizer = new User(); organizer.setId(1L);
//        User coach = new User(); coach.setId(2L);
//        Tournament tournament = new Tournament(); tournament.setId(10L); tournament.setOrganizer(organizer);
//        TeamRequest request = new TeamRequest("Team A", "Poland", "coach@example.com");
//
//        when(tournamentRepository.findById(10L)).thenReturn(Optional.of(tournament));
//        when(userRepository.findByEmail("coach@example.com")).thenReturn(Optional.of(coach));
//        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Team created = teamService.createTeam(10L, request, organizer);
//
//        assertEquals("Team A", created.getName());
//        assertEquals("Poland", created.getCountry());
//        assertEquals(coach, created.getCoach());
//        assertEquals(tournament, created.getTournament());
//    }
//
//    @Test
//    void updateTeam_updatesAndReturnsTeam() {
//        User organizer = new User(); organizer.setId(1L);
//        User newCoach = new User(); newCoach.setId(3L);
//        Tournament tournament = new Tournament(); tournament.setId(10L); tournament.setOrganizer(organizer);
//        Team team = new Team(); team.setId(100L); team.setTournament(tournament); team.setCoach(organizer);
//        TeamRequest request = new TeamRequest("Updated", "Germany", "new@coach.com");
//
//        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
//        when(userRepository.findByEmail("new@coach.com")).thenReturn(Optional.of(newCoach));
//        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        Team updated = teamService.updateTeam(10L, 100L, request, organizer);
//
//        assertEquals("Updated", updated.getName());
//        assertEquals("Germany", updated.getCountry());
//        assertEquals(newCoach, updated.getCoach());
//    }
//
//    @Test
//    void deleteTeam_deletesIfAuthorized() {
//        User organizer = new User(); organizer.setId(1L);
//        Tournament tournament = new Tournament(); tournament.setId(10L); tournament.setOrganizer(organizer);
//        Team team = new Team(); team.setId(100L); team.setTournament(tournament); team.setCoach(organizer);
//
//        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
//
//        teamService.deleteTeam(10L, 100L, organizer);
//
//        verify(teamRepository, times(1)).delete(team);
//    }
//
//    @Test
//    void addPlayer_addsPlayerToTeam() {
//        User user = new User(); user.setId(1L);
//        Tournament tournament = new Tournament(); tournament.setId(10L); tournament.setOrganizer(user);
//        Team team = new Team(); team.setId(100L); team.setTournament(tournament); team.setCoach(user);
//        team.setPlayerList(new ArrayList<>());
//
//        Player request = new Player();
//        request.setName("John Doe");
//        request.setNumber(10);
//        request.setBirthDate(LocalDate.of(2000, 1, 1));
//
//        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
//        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));
//        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        Team result = teamService.addPlayer(10L, 100L, request, user);
//
//        assertEquals(1, result.getPlayerList().size());
//        assertEquals("John Doe", result.getPlayerList().get(0).getName());
//    }
//
//    @Test
//    void updatePlayer_updatesPlayerFields() {
//        User user = new User(); user.setId(1L);
//        Tournament tournament = new Tournament(); tournament.setId(10L); tournament.setOrganizer(user);
//        Team team = new Team(); team.setId(100L); team.setTournament(tournament); team.setCoach(user);
//
//        Player player = new Player();
//        player.setId(200L);
//        player.setTeam(team);
//
//        Player updated = new Player();
//        updated.setName("New Name");
//        updated.setNumber(9);
//        updated.setBirthDate(LocalDate.of(1999, 12, 12));
//
//        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
//        when(playerRepository.findById(200L)).thenReturn(Optional.of(player));
//        when(playerRepository.save(any(Player.class))).thenReturn(player);
//        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
//
//        Team result = teamService.updatePlayer(10L, 100L, 200L, updated, user);
//
//        verify(playerRepository).save(player);
//        assertEquals("New Name", player.getName());
//        assertEquals(9, player.getNumber());
//    }
//
//    @Test
//    void removePlayer_removesPlayerFromTeam() {
//        User user = new User(); user.setId(1L);
//        Tournament tournament = new Tournament(); tournament.setId(10L); tournament.setOrganizer(user);
//        Team team = new Team(); team.setId(100L); team.setTournament(tournament); team.setCoach(user);
//
//        Player player = new Player(); player.setId(200L); player.setTeam(team);
//        team.setPlayerList(new ArrayList<>(List.of(player)));
//
//        when(teamRepository.findById(100L)).thenReturn(Optional.of(team));
//        when(playerRepository.findById(200L)).thenReturn(Optional.of(player));
//
//        Team result = teamService.removePlayer(10L, 100L, 200L, user);
//
//        verify(playerRepository).delete(player);
//        assertEquals(0, result.getPlayerList().size());
//    }
//
//}
