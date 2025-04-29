package com.tournament.app.footycup.backend.model;


import com.tournament.app.footycup.backend.enums.TournamentStatus;
import com.tournament.app.footycup.backend.enums.TournamentSystem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tournaments")
public class Tournament implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_organizer", nullable = false, updatable = false)
    private User organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status = TournamentStatus.UPCOMING;

    @Enumerated(EnumType.STRING)
    private TournamentSystem system;

    public Tournament(Long id, String name, LocalDate startDate, LocalDate endDate, String location, User organizer, TournamentStatus status) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.organizer = organizer;
        this.status = status;
    }

    public Tournament(String name, LocalDate startDate, LocalDate endDate, String location, User organizer, TournamentStatus status) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.organizer = organizer;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", organizer=" + organizer +
                ", status=" + status +
                '}';
    }
}
