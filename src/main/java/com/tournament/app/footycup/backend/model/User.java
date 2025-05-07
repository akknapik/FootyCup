package com.tournament.app.footycup.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.tournament.app.footycup.backend.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Schema(description = "User entity representing an application user")
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User implements Serializable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique user ID", example = "1")
    private Long id;

    @Schema(description = "First name of the user", example = "John")
    @Column(nullable = false)
    private String firstname;

    @Schema(description = "Last name of the user", example = "Doe")
    @Column(nullable = false)
    private String lastname;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    @Column(nullable = false)
    private String email;

    @Schema(description = "User's password (hashed)", example = "$2a$10$...")
    @Column(nullable = false)
    private String password;

    @Schema(description = "User's role in the system", example = "USER")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole = UserRole.USER;

    @Schema(description = "Whether the account is locked", example = "false")
    private Boolean locked = false;

    @Schema(description = "Whether the account is enabled", example = "false")
    private Boolean enabled = false;

    public User(String firstname,
                String lastname,
                String email,
                String password,
                UserRole userRole,
                Boolean locked,
                Boolean enabled) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
        this.locked = locked;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole));

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
