package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByTournamentId(Long tournamentId);
    List<Match> findByGroupId(Long groupId);
    List<Match> findByRefereeId(Long refereeId);
    List<Match> findByTournamentIdAndRefereeId(Long tournamentId, Long refereeId);

    @Query("""
    SELECT m FROM Match m
    WHERE (m.teamHome = :team OR m.teamAway = :team)
      AND m.group.id = :groupId
    """)
    public List<Match> findByTeamInGroup(Team team, Long groupId);

    @Query("""
    SELECT m FROM Match m
    WHERE m.tournament.id = :tournamentId
      AND (
        (m.teamHome IS NOT NULL AND m.teamHome.id = :teamId)
        OR (m.teamAway IS NOT NULL AND m.teamAway.id = :teamId)
      )
    """)
    List<Match> findByTournamentIdAndTeamId(Long tournamentId, Long teamId);
}
