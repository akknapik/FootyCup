package com.tournament.app.footycup.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tactics_board",
        uniqueConstraints = @UniqueConstraint(name = "uq_tactics_board_match_team", columnNames = {"match_id", "team_id"}))
public class TacticsBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Lob
    @Column(name = "state_json", nullable = false)
    private String stateJson;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}