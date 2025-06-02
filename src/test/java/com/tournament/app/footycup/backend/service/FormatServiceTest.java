package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.enums.MatchStatus;
import com.tournament.app.footycup.backend.model.*;
import com.tournament.app.footycup.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FormatServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupTeamRepository groupTeamRepository;
    @Mock
    private BracketNodeRepository bracketNodeRepository;
    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchService matchService;

    @InjectMocks
    private FormatService formatService;

    private Tournament tournament;
    private User organizer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        organizer = new User();
        organizer.setId(1L);

        tournament = new Tournament();
        tournament.setId(100L);
        tournament.setOrganizer(organizer);
    }

    @Test
    void generateGroupStructure_shouldCreateGroupsAndSlots() {
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));

        formatService.generateGroupStructure(100L, 2, 3, organizer);

        verify(groupRepository, times(2)).save(any(Group.class));
        verify(groupTeamRepository, times(6)).save(any(GroupTeam.class));
    }

    @Test
    void assignTeamsRandomlyToGroups_shouldAssignTeams() {
        Team t1 = new Team();
        t1.setId(1L);
        Team t2 = new Team();
        t2.setId(2L);
        Team t3 = new Team();
        t3.setId(3L);

        GroupTeam slot1 = new GroupTeam();
        slot1.setId(1L);
        slot1.setTeam(null);
        GroupTeam slot2 = new GroupTeam();
        slot2.setId(2L);
        slot2.setTeam(null);

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(groupRepository.findByTournamentId(100L)).thenReturn(List.of(new Group()));
        when(teamRepository.findByTournamentId(100L)).thenReturn(List.of(t1, t2, t3));
        when(groupTeamRepository.findByGroupTournamentId(100L)).thenReturn(List.of(slot1, slot2));

        formatService.assignTeamsRandomlyToGroups(100L, organizer);

        verify(groupTeamRepository, times(2)).save(any(GroupTeam.class));
    }

    @Test
    void generateBracketStructure_shouldCreateCorrectMatchTree() {
        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));

        formatService.generateBracketStructure(100L, 4, organizer);

        verify(matchRepository, times(3)).save(any(Match.class));
        verify(bracketNodeRepository, times(3)).save(any(BracketNode.class));
    }

    @Test
    void assignTeamToSlot_shouldAssignTeam() {
        GroupTeam slot = new GroupTeam();
        slot.setId(10L);
        Team team = new Team();
        team.setId(20L);

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(groupTeamRepository.findById(10L)).thenReturn(Optional.of(slot));
        when(teamRepository.findById(20L)).thenReturn(Optional.of(team));

        formatService.assignTeamToSlot(100L, 10L, 20L, organizer);

        verify(groupTeamRepository).save(slot);
        assertEquals(team, slot.getTeam());
    }

    @Test
    void getGroupsWithStandings_shouldComputeStats() {
        Team team = new Team();
        team.setId(1L);
        Group group = new Group();
        group.setId(2L);

        GroupTeam gt = new GroupTeam();
        gt.setGroup(group);
        gt.setTeam(team);
        group.setGroupTeams(new ArrayList<>(List.of(gt)));

        Match match = new Match();
        match.setTeamHome(team);
        match.setTeamAway(new Team());
        match.setHomeScore(2);
        match.setAwayScore(1);

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(groupRepository.findByTournamentId(100L)).thenReturn(List.of(group));
        when(matchRepository.findByTeamInGroup(eq(team), eq(group.getId()))).thenReturn(List.of(match));

        List<Group> result = formatService.getGroupsWithStandings(100L, organizer);

        assertEquals(3, gt.getPoints());
        assertEquals(2, gt.getGoalsFor());
        assertEquals(1, gt.getGoalsAgainst());
        assertEquals(1, result.size());
    }
}

