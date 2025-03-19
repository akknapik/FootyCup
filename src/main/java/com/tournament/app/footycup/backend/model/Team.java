package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String coach;

    private int numberOfPlayer = 0;

    @OneToMany(mappedBy = "team", orphanRemoval = true)
    @JsonManagedReference
    private List<Player> playerList = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private String tournamentCode;

    public Team() {
    }

    public Team(Long id, String name, String coach, int numberOfPlayer, List<Player> playerList, String tournamentCode) {
        this.id = id;
        this.name = name;
        this.coach = coach;
        this.numberOfPlayer = numberOfPlayer;
        this.playerList = playerList;
        this.tournamentCode = tournamentCode;
    }

    public Team(String name, String coach, int numberOfPlayer, List<Player> playerList, String tournamentCode) {
        this.name = name;
        this.coach = coach;
        this.numberOfPlayer = numberOfPlayer;
        this.playerList = playerList;
        this.tournamentCode = tournamentCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public int getNumberOfPlayer() {
        return numberOfPlayer;
    }

    public void setNumberOfPlayer(int numberOfPlayer) {
        this.numberOfPlayer = numberOfPlayer;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public String getTournamentCode() {
        return tournamentCode;
    }

    public void setTournamentCode(String tournamentCode) {
        this.tournamentCode = tournamentCode;
    }
}
