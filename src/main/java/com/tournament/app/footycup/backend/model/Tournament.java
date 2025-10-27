package com.tournament.app.footycup.backend.model;

import com.tournament.app.footycup.backend.enums.TournamentStatus;
import com.tournament.app.footycup.backend.enums.TournamentSystem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Schema(description = "Tournament entity representing a football competition")
@Entity
@Table(
        name = "tournaments",
        indexes = {
                @Index(name = "idx_tournament_name", columnList = "name"),
                @Index(name = "idx_tournament_start_date", columnList = "startDate"),
                @Index(name = "idx_tournament_organizer", columnList = "id_organizer")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tournament implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique tournament ID", example = "101")
    private Long id;

    @Schema(description = "Name of the tournament", example = "Summer Cup")
    @Column(nullable = false)
    private String name;

    @Schema(description = "Start date of the tournament", example = "2025-06-01")
    @Column(nullable = false)
    private LocalDate startDate;

    @Schema(description = "End date of the tournament", example = "2025-06-10")
    @Column(nullable = false)
    private LocalDate endDate;

    @Schema(description = "Location of the tournament", example = "Madrid")
    private String location;

    @Schema(description = "Organizer of the tournament")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_organizer", nullable = false, updatable = false)
    private User organizer;

    @Schema(description = "Tournament status", example = "UPCOMING")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status = TournamentStatus.UPCOMING;

    @Schema(description = "Tournament system type", example = "GROUP_STAGE")
    @Enumerated(EnumType.STRING)
    private TournamentSystem system;

    @Schema(description = "Determines if the tournament is visible to all users", example = "true")
    @Column(name = "is_public", nullable = false)
    private boolean publicVisible = false;

    @Schema(description = "Referees assigned to the tournament")
    @ManyToMany
    @JoinTable(name = "tournament_referees",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "referee_id"))
    private List<User> referees = new ArrayList<>();

    @Schema(description = "Scoring rules for the tournament (e.g. WIN = 3)")
    @ElementCollection
    @CollectionTable(name = "scoring_rules", joinColumns = @JoinColumn(name = "tournament_id"))
    @MapKeyColumn(name = "result_type")
    @Column(name = "points")
    private Map<String, Integer> scoringRules = new HashMap<>(Map.of(
            "WIN", 3,
            "DRAW", 1,
            "LOSS", 0
    ));
}
