package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_teams")
@Schema(description = "Represents a team's participation and performance within a group")
public class GroupTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique group-team entry ID", example = "11")
    private Long id;

    @Schema(description = "Group this entry belongs to")
    @ManyToOne
    @JoinColumn(name = "id_group")
    @JsonBackReference
    private Group group;

    @Schema(description = "Team participating in the group")
    @ManyToOne
    @JoinColumn(name = "id_team")
    private Team team;

    @Schema(description = "Team's current position in the group", example = "2")
    private Integer position;

    @Schema(description = "Total points earned", example = "6")
    private Integer points = 0;

    @Schema(description = "Goals scored by the team", example = "5")
    private Integer goalsFor = 0;

    @Schema(description = "Goals conceded by the team", example = "2")
    private Integer goalsAgainst = 0;
}
