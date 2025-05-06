package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    public Schedule findByTournamentId(Long tournamentId);
}
