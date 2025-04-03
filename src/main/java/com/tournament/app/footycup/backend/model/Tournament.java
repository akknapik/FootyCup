package com.tournament.app.footycup.backend.model;


import com.tournament.app.footycup.backend.enums.TournamentStatus;
import com.tournament.app.footycup.backend.enums.TournamentSystem;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

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

    public Tournament() {
    }

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public TournamentSystem getSystem() {
        return system;
    }

    public void setSystem(TournamentSystem system) {
        this.system = system;
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
