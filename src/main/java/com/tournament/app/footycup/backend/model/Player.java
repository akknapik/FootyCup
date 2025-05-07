package com.tournament.app.footycup.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "players")
@Schema(description = "Player entity representing a football team member")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique player ID", example = "301")
    private Long id;

    @Schema(description = "Jersey number of the player", example = "10")
    private Integer number;

    @Schema(description = "Full name of the player", example = "Lionel Messi")
    private String name;

    @Schema(description = "Date of birth of the player", example = "1987-06-24")
    private LocalDate birthDate;

    @Schema(description = "Team to which the player belongs")
    @ManyToOne
    @JoinColumn(name = "id_team")
    @JsonBackReference
    private Team team;
}
