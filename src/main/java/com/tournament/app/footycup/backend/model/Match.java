package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tournament.app.footycup.backend.enums.MatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "matches")
@Schema(description = "Match entity representing a scheduled football match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique match ID", example = "1001")
    @Column(nullable = false, updatable = false)
    private Long id;

    @Schema(description = "Optional match name or label", example = "Group A - Match 1")
    @Column
    private String name;

    @Schema(description = "Tournament the match belongs to")
    @ManyToOne
    @JoinColumn(name = "id_tournament")
    @JsonIgnore
    private Tournament tournament;

    @Schema(description = "Home team")
    @ManyToOne
    @JoinColumn(name = "id_team_home")
    private Team teamHome;

    @Schema(description = "Away team")
    @ManyToOne
    @JoinColumn(name = "id_team_away")
    private Team teamAway;

    @Schema(description = "Date of the match", example = "2025-06-10")
    private LocalDate matchDate;

    @Schema(description = "Start time of the match", example = "15:30")
    private LocalTime matchTime;

    @Schema(description = "Current status of the match", example = "SCHEDULED")
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    @Schema(description = "Duration of the match in minutes", example = "15")
    private int durationInMin = 15;

    @Schema(description = "Score of the home team", example = "2")
    private Integer homeScore;

    @Schema(description = "Score of the away team", example = "1")
    private Integer awayScore;

    @Schema(description = "Group in which the match is played, if applicable")
    @ManyToOne
    @JoinColumn(name = "id_group")
    private Group group;
}
