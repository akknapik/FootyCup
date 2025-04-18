package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;

public interface TournamentRepository extends JpaRepositoryImplementation<Tournament, Long> {
    List<Tournament> findAllByOrganizerId(Long organizerId);
    List<Tournament> findByOrganizer(User organizer);

}
