package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_teams")
public class GroupTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_group")
    @JsonBackReference
    private Group group;

    @ManyToOne
    @JoinColumn(name = "id_team")
    private Team team;

    private Integer position;
    private Integer points = 0;
    private Integer goalsFor = 0;
    private Integer goalsAgainst = 0;
}
