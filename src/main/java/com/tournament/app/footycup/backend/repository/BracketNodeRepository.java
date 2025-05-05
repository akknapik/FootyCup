package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.BracketNode;
import com.tournament.app.footycup.backend.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BracketNodeRepository extends JpaRepository<BracketNode, Long> {
    List<BracketNode> findByTournamentId(Long id);
    BracketNode findByMatch(Match match);
    List<BracketNode> findByParentHomeNodeOrParentAwayNode(BracketNode currentNode, BracketNode currentNode2);
}
