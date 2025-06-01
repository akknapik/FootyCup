package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {
    @Query("SELECT se.match.id FROM ScheduleEntry se WHERE se.match IS NOT NULL AND se.schedule.tournament.id = :tournamentId")
    public List<Long> findAllScheduledMatchIdsByTournamentId(Long tournamentId);

    public List<ScheduleEntry> findByScheduleId(Long scheduleId);
}
