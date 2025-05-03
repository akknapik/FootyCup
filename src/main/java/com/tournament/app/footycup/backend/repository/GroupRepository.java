package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Group;
import com.tournament.app.footycup.backend.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByTournamentId(Long id);
}
