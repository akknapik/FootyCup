package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTournamentId(Long tournamentId);
    Team findTournamentByTournamentId(Long tournamentId);
    boolean existsByTournamentIdAndCoach_Id(Long tournamentId, Long coachId);

    List<Team> findByCoach_Id(Long coachId);
}
