package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Team entity representing a football team")
@Entity
@Table(
        name = "teams",
        indexes = {
                @Index(name = "idx_team_name", columnList = "name"),
                @Index(name = "idx_team_tournament", columnList = "id_tournament")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique team ID", example = "501")
    private Long id;

    @Schema(description = "Name of the team", example = "FC Dragons")
    @Column(nullable = false)
    private String name;

    @Schema(description = "Coach of the team")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_coach")
    private User coach;

    @Schema(description = "Country represented by the team", example = "Germany")
    private String country;

    @Schema(description = "Tournament in which the team participates")
    @ManyToOne
    @JoinColumn(name = "id_tournament", nullable = false, updatable = false)
    private Tournament tournament;

    @Schema(description = "List of players in the team")
    @OneToMany(mappedBy = "team", orphanRemoval = true)
    @JsonManagedReference
    private List<Player> playerList = new ArrayList<>();
}
