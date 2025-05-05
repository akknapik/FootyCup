package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    public List<Match> findByTournamentId(Long tournamentId);
}
