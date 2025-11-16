package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepositoryImplementation<Tournament, Long> {
    List<Tournament> findByOrganizer(User organizer);

    @EntityGraph(attributePaths = {"referees", "followers"})
    Optional<Tournament> findWithRefereesById(Long id);

    List<Tournament> findByPublicVisibleTrueOrderByStartDateAsc();

    List<Tournament> findDistinctByReferees_Id(Long refereeId);

    List<Tournament> findDistinctByFollowers_Id(Long userId);

    Optional<Tournament> findByCode(String code);

    boolean existsByCode(String code);
}
