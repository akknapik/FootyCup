package com.tournament.app.footycup.backend.model;

import com.tournament.app.footycup.backend.enums.MatchStatus;
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
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_tournament")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "id_team_home")
    private Team teamHome;

    @ManyToOne
    @JoinColumn(name = "id_team_away")
    private Team teamAway;

    private LocalDate matchDate;

    private LocalTime matchTime;

    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    private int durationInMin = 15;

    private Integer homeScore;
    private Integer awayScore;

    @ManyToOne
    @JoinColumn(name = "id_group")
    private Group group;

}
