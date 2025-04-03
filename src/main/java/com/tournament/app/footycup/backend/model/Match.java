package com.tournament.app.footycup.backend.model;

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

    @ManyToOne
    @JoinColumn(name = "id_home_team")
    private Team home_team;

    @ManyToOne
    @JoinColumn(name = "id_away_team")
    private Team away_team;

    private LocalDate date;

    private LocalTime time;

    private int durationInMin;

    @OneToOne
    @JoinColumn(name = "id_result")
    private Result result;

    @ManyToOne
    @JoinColumn(name = "id_tournament")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "id_group")
    private Group group;
}
