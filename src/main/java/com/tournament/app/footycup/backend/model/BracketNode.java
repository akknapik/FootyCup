package com.tournament.app.footycup.backend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bracketNodes")
public class BracketNode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    private Integer round;
    private Integer position;

    @ManyToOne
    @JoinColumn(name = "id_parent_home_node")
    private BracketNode parentHomeNode;

    @ManyToOne
    @JoinColumn(name = "id_parent_away_node")
    private BracketNode parentAwayNode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_match")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "id_tournament")
    private Tournament tournament;

}
