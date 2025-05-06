package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tournament.app.footycup.backend.enums.EntryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedule_entries")
public class ScheduleEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_schedule")
    @JsonBackReference
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    private EntryType type;

    @ManyToOne
    @JoinColumn(name = "id_match")
    private Match match;

    private LocalDateTime startDateTime;

    private int durationInMin;
}
