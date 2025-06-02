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

class MatchServiceTest {

    @Mock private GroupRepository groupRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private TournamentRepository tournamentRepository;
    @Mock private BracketNodeRepository bracketNodeRepository;

    @InjectMocks private MatchService matchService;

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
    }

    @Test
    void getMatches_shouldReturnListOfMatches() {
        Match match1 = new Match();
        Match match2 = new Match();
        List<Match> matches = List.of(match1, match2);

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(matchRepository.findByTournamentId(100L)).thenReturn(matches);

        List<Match> result = matchService.getMatches(100L, organizer);

        assertEquals(2, result.size());
        verify(matchRepository).findByTournamentId(100L);
    }

    @Test
    void getMatch_shouldReturnSingleMatch() {
        Match match = new Match();
        match.setId(200L);

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(matchRepository.findById(200L)).thenReturn(Optional.of(match));

        Match result = matchService.getMatch(100L, 200L, organizer);

        assertEquals(200L, result.getId());
        verify(matchRepository).findById(200L);
    }

    @Test
    void deleteMatch_shouldCallDelete() {
        Match match = new Match();
        match.setId(200L);

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(matchRepository.findById(200L)).thenReturn(Optional.of(match));

        matchService.deleteMatch(100L, 200L, organizer);

        verify(matchRepository).delete(match);
    }

    @Test
    void updateSingleMatchResult_shouldUpdateAndPropagate() {
        Match match = new Match();
        match.setId(300L);
        match.setTournament(tournament);
        match.setTeamHome(new Team());
        match.setTeamAway(new Team());
        match.setHomeScore(0);
        match.setAwayScore(0);

        Match updated = new Match();
        updated.setHomeScore(2);
        updated.setAwayScore(1);

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(matchRepository.findById(300L)).thenReturn(Optional.of(match));
        when(bracketNodeRepository.findByMatch(match)).thenReturn(null);

        matchService.updateSingleMatchResult(100L, 300L, updated, organizer);

        assertEquals(2, match.getHomeScore());
        assertEquals(1, match.getAwayScore());
        verify(matchRepository).save(match);
    }

    @Test
    void generateGroupMatches_shouldGenerateCorrectNumberOfMatches() {
        Team t1 = new Team(); t1.setId(1L);
        Team t2 = new Team(); t2.setId(2L);
        Team t3 = new Team(); t3.setId(3L);

        GroupTeam gt1 = new GroupTeam(); gt1.setTeam(t1);
        GroupTeam gt2 = new GroupTeam(); gt2.setTeam(t2);
        GroupTeam gt3 = new GroupTeam(); gt3.setTeam(t3);

        Group group = new Group();
        group.setId(10L);
        group.setName("GroupA");
        group.setGroupTeams(List.of(gt1, gt2, gt3));

        when(tournamentRepository.findById(100L)).thenReturn(Optional.of(tournament));
        when(groupRepository.findByTournamentId(100L)).thenReturn(List.of(group));

        matchService.generateGroupMatches(100L, organizer);

        verify(matchRepository, times(3)).save(any(Match.class));
    }
}
