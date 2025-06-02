package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "groups",
        indexes = {
                @Index(name = "idx_group_tournament", columnList = "id_tournament")
        }
)@Schema(description = "Group of teams in a tournament (used for group stage format)")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique group ID", example = "1")
    @Column(nullable = false, updatable = false)
    private Long id;

    @Schema(description = "Group name (e.g., Group A)", example = "Group A")
    private String name;

    @Schema(description = "Tournament this group belongs to")
    @ManyToOne
    @JoinColumn(name = "id_tournament", nullable = false, updatable = false)
    private Tournament tournament;

    @Schema(description = "List of teams in this group")
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<GroupTeam> groupTeams = new ArrayList<>();
}
