package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTournamentId(Long tournamentId);
    Team findTournamentByTournamentId(Long tournamentId);
}
