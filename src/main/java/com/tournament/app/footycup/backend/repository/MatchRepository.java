package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    public List<Match> findByTournamentId(Long tournamentId);
    public List<Match> findByGroupId(Long groupId);

    @Query("""
    SELECT m FROM Match m
    WHERE (m.teamHome = :team OR m.teamAway = :team)
      AND m.group.id = :groupId
""")
    public List<Match> findByTeamInGroup(Team team, Long groupId);
}
