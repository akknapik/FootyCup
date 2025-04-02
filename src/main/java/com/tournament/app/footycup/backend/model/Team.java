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

    @ManyToOne
    @JoinColumn(name = "id_tournament", nullable = false, updatable = false)
    private Tournament tournament;

    @OneToMany(mappedBy = "team", orphanRemoval = true)
    @JsonManagedReference
    private List<Player> playerList = new ArrayList<>();

    public Team() {
    }

    public Team(Long id, String name, String coach, Tournament tournament, List<Player> playerList) {
        this.id = id;
        this.name = name;
        this.coach = coach;
        this.tournament = tournament;
        this.playerList = playerList;
    }

    public Team(String name, String coach, Tournament tournament, List<Player> playerList) {
        this.name = name;
        this.coach = coach;
        this.tournament = tournament;
        this.playerList = playerList;
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

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
}
