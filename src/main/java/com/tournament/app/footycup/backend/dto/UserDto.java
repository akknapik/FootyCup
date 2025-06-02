package com.tournament.app.footycup.backend.dto;

import com.tournament.app.footycup.backend.enums.UserRole;
import com.tournament.app.footycup.backend.model.User;

import java.io.Serializable;
import java.util.Objects;

public class UserDto implements Serializable {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private UserRole userRole;

    public UserDto() {
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.userRole = user.getUserRole();
    }

    public UserDto(Long id, String firstname, String lastname, String email, UserRole userRole) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto entity = (UserDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.firstname, entity.firstname) &&
                Objects.equals(this.lastname, entity.lastname) &&
                Objects.equals(this.email, entity.email) &&
                Objects.equals(this.userRole, entity.userRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "firstname = " + firstname + ", " +
                "lastname = " + lastname + ", " +
                "email = " + email + ")";
    }
}