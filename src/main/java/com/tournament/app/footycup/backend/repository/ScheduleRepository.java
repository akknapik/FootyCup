package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    public List<Schedule> findByTournamentId(Long tournamentId);
    public List<Schedule> findByTournamentIdOrderByStartDateTimeAsc(Long tournamentId);

}
