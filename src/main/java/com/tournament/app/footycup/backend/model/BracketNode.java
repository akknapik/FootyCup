package com.tournament.app.footycup.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "bracketNodes",
        indexes = {
                @Index(name = "idx_bracket_node_position", columnList = "position"),
                @Index(name = "idx_bracket_node_parent_home", columnList = "id_parent_home_node"),
                @Index(name = "idx_bracket_node_parent_away", columnList = "id_parent_away_node"),
                @Index(name = "idx_bracket_node_tournament", columnList = "id_tournament")
        }
)@Schema(description = "Represents a single node in a tournament bracket (elimination format)")
public class BracketNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique bracket node ID", example = "100")
    @Column(nullable = false, updatable = false)
    private Long id;

    @Schema(description = "Round number (e.g. 1 for quarterfinals)", example = "1")
    private Integer round;

    @Schema(description = "Node position in its round", example = "2")
    private Integer position;

    @Schema(description = "Parent node for home team (if applicable)")
    @ManyToOne
    @JoinColumn(name = "id_parent_home_node")
    private BracketNode parentHomeNode;

    @Schema(description = "Parent node for away team (if applicable)")
    @ManyToOne
    @JoinColumn(name = "id_parent_away_node")
    private BracketNode parentAwayNode;

    @Schema(description = "Match associated with this bracket node")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_match")
    private Match match;

    @Schema(description = "Tournament this node belongs to")
    @ManyToOne
    @JoinColumn(name = "id_tournament")
    private Tournament tournament;
}
