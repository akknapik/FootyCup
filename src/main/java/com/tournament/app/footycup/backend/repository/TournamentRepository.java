package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Tournament;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface TournamentRepository extends JpaRepositoryImplementation<Tournament, Long> {
    Tournament findTournamentById(Long id);
    List<Tournament> findAllByOrganizerId(Long organizerId);
}
