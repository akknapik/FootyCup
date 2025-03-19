package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int number;

    private String name;

    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "id_team")
    @JsonBackReference
    private Team team;

    @Column(nullable = false, updatable = false)
    private String tournamentCode;

    public Player() {
    }

    public Player(Long id, int number, String tournamentCode, String name, LocalDate birthDate, Team team) {
        this.id = id;
        this.number = number;
        this.tournamentCode = tournamentCode;
        this.name = name;
        this.birthDate = birthDate;
        this.team = team;
    }

    public Player(int number, String tournamentCode, String name, LocalDate birthDate, Team team) {
        this.number = number;
        this.tournamentCode = tournamentCode;
        this.name = name;
        this.birthDate = birthDate;
        this.team = team;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTournamentCode() {
        return tournamentCode;
    }

    public void setTournamentCode(String tournamentCode) {
        this.tournamentCode = tournamentCode;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
