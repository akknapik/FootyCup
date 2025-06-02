package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tournament.app.footycup.backend.enums.EntryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "schedule_entries",
        indexes = {
                @Index(name = "idx_schedule_entry_schedule", columnList = "id_schedule"),
                @Index(name = "idx_schedule_entry_type", columnList = "type"),
                @Index(name = "idx_schedule_entry_match", columnList = "id_match")
        }
)@Schema(description = "Single entry in a schedule, either a match or a break")
public class ScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique schedule entry ID", example = "10")
    private Long id;

    @Schema(description = "Schedule this entry belongs to")
    @ManyToOne
    @JoinColumn(name = "id_schedule")
    @JsonBackReference
    private Schedule schedule;

    @Schema(description = "Type of schedule entry (MATCH or BREAK)", example = "MATCH")
    @Enumerated(EnumType.STRING)
    private EntryType type;

    @Schema(description = "Match associated with this schedule entry (if applicable)")
    @ManyToOne
    @JoinColumn(name = "id_match")
    private Match match;

    @Schema(description = "Start time of this schedule entry", example = "2025-06-01T10:30:00")
    private LocalDateTime startDateTime;

    @Schema(description = "Duration of this entry in minutes", example = "15")
    private int durationInMin;
}
