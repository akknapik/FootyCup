package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;

    private String name;

    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "id_team")
    @JsonBackReference
    private Team team;

    @Column(nullable = false, updatable = false)
    private String playerCode;

    public Player() {
    }

    public Player(Long id, Integer number, String playerCode, String name, LocalDate birthDate, Team team) {
        this.id = id;
        this.number = number;
        this.playerCode = playerCode;
        this.name = name;
        this.birthDate = birthDate;
        this.team = team;
    }

    public Player(Integer number, String playerCode, String name, LocalDate birthDate, Team team) {
        this.number = number;
        this.playerCode = playerCode;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
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

    public String getPlayerCode() {
        return playerCode;
    }

    public void setPlayerCode(String playerCode) {
        this.playerCode = playerCode;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
