package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.MatchEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {
    List<MatchEvent> findByMatchIdOrderByMinuteDesc(Long matchId);
}
