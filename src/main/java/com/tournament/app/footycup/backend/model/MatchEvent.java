package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tournament.app.footycup.backend.enums.MatchEventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Represents a single event occurring during a match")
@Entity
@Table(name = "match_events", indexes = {
        @Index(name = "idx_match_event_match", columnList = "id_match"),
        @Index(name = "idx_match_event_type", columnList = "event_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_match", nullable = false)
    @JsonIgnore
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_player")
    @JsonIgnoreProperties({"team"})
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_secondary_player")
    @JsonIgnoreProperties({"team"})
    private Player secondaryPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_team")
    @JsonIgnoreProperties({"playerList", "tournament"})
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private MatchEventType eventType;

    @Column(nullable = false)
    private Integer minute;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recorded_by", nullable = false)
    @JsonIgnoreProperties({"password"})
    private User recordedBy;
}

