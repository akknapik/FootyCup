package com.tournament.app.footycup.backend.dto;

import com.tournament.app.footycup.backend.enums.TournamentStatus;
import com.tournament.app.footycup.backend.model.Tournament;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * DTO for {@link com.tournament.app.footycup.backend.model.Tournament}
 */
public class TournamentDto implements Serializable {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private UserDto organizer;
    private TournamentStatus status = TournamentStatus.UPCOMING;

    public TournamentDto() {
    }

    public TournamentDto(Long id, String name, LocalDate startDate, LocalDate endDate, UserDto organizer, TournamentStatus status) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organizer = organizer;
        this.status = status;
    }

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.name = tournament.getName();
        this.startDate = tournament.getStartDate();
        this.endDate = tournament.getEndDate();
        this.organizer = new UserDto(tournament.getOrganizer());
        this.status = tournament.getStatus();
    }

    public TournamentDto(Optional<Tournament> tournament) {
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

    public UserDto getOrganizer() {
        return organizer;
    }

    public void setOrganizer(UserDto organizer) {
        this.organizer = organizer;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentDto entity = (TournamentDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.name, entity.name) &&
                Objects.equals(this.startDate, entity.startDate) &&
                Objects.equals(this.endDate, entity.endDate) &&
                Objects.equals(this.organizer, entity.organizer) &&
                Objects.equals(this.status, entity.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startDate, endDate, organizer, status);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "startDate = " + startDate + ", " +
                "endDate = " + endDate + ", " +
                "organizer = " + organizer + ", " +
                "status = " + status + ")";
    }
}