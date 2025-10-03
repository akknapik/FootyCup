package com.tournament.app.footycup.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "schedules",
        indexes = {
                @Index(name = "idx_schedule_tournament", columnList = "id_tournament"),
                @Index(name = "idx_schedule_start_datetime", columnList = "startDateTime")
        }
)@Schema(description = "Schedule for a tournament including matches and breaks")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique schedule ID", example = "1")
    private Long id;

    @Schema(description = "Tournament associated with this schedule")
    @ManyToOne
    @JoinColumn(name = "id_tournament")
    private Tournament tournament;

    @Schema(description = "Start date and time of the schedule", example = "2025-06-01T10:00:00")
    private LocalDateTime startDateTime;

    @Schema(description = "List of schedule entries (matches or breaks)")
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDateTime")
    private List<ScheduleEntry> entries = new ArrayList<>();

    @Schema(description = "Break duration between matches in minutes", example = "10")
    @Column(name = "break_between_matches_in_min")
    private int breakBetweenMatchesInMin;
}
