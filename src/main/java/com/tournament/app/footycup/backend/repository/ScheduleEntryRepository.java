package com.tournament.app.footycup.backend.repository;

import com.tournament.app.footycup.backend.model.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {
}
