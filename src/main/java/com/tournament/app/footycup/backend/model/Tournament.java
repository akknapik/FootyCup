package com.tournament.app.footycup.backend.model;


import com.tournament.app.footycup.backend.enums.TournamentStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private LocalDate endDate;

    @Column(nullable = false, updatable = false)
    private Long id_organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false, updatable = false)
    private String tournamentCode;

    public Tournament() {
    }

    public Tournament(Long id,
                      String name,
                      LocalDate startDate,
                      LocalDate endDate,
                      Long id_organizer,
                      TournamentStatus status,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt,
                      String tournamentCode) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.id_organizer = id_organizer;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tournamentCode = tournamentCode;
    }

    public Tournament(String name,
                      LocalDate startDate,
                      LocalDate endDate,
                      Long id_organizer,
                      TournamentStatus status,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt,
                      String tournamentCode) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.id_organizer = id_organizer;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Long getId_organizer() {
        return id_organizer;
    }

    public void setId_organizer(Long id_organizer) {
        this.id_organizer = id_organizer;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTournamentCode() {
        return tournamentCode;
    }

    public void setTournamentCode(String tournamentCode) {
        this.tournamentCode = tournamentCode;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", id_organizer=" + id_organizer +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", tournamentCode='" + tournamentCode + '\'' +
                '}';
    }
}
