package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.TacticsBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TacticsBoardRepository extends JpaRepository<TacticsBoard, Long> {
    Optional<TacticsBoard> findByMatch(Match match);
    Optional<TacticsBoard> findByMatchId(Long matchId);
    void deleteByMatch(Match match);
}