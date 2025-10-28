package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.TacticsBoard;
import com.tournament.app.footycup.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TacticsBoardRepository extends JpaRepository<TacticsBoard, Long> {
    Optional<TacticsBoard> findByMatchAndTeam(Match match, Team team);
    Optional<TacticsBoard> findByMatchAndTeamIsNull(Match match);
    void deleteByMatchAndTeam(Match match, Team team);
}