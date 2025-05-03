package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.GroupTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupTeamRepository extends JpaRepository<GroupTeam, Long> {
    List<GroupTeam> findByGroupTournamentId(Long tournamentId);
}
