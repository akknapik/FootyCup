package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.UserDto;
import com.tournament.app.footycup.backend.dto.account.ChangePasswordRequest;
import com.tournament.app.footycup.backend.dto.account.DeleteAccountRequest;
import com.tournament.app.footycup.backend.dto.account.UpdateProfileRequest;
import com.tournament.app.footycup.backend.enums.UserRole;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import com.tournament.app.footycup.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentService tournamentService;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDto addUser(User user) {
        return new UserDto(userRepository.save(user));
    }

    public List<UserDto> getAllUsers(User user) {
        if(!user.getUserRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("Lack of authorization");
        }
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDto::new).collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());

        return new UserDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, User newUser, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());
        if(!user.getUserRole().equals(UserRole.ADMIN) && !user.getId().equals(id)) {
            throw new AccessDeniedException("Lack of authorization");
        }

        if (newUser.getFirstname() != null) existingUser.setFirstname(newUser.getFirstname());
        if (newUser.getLastname() != null) existingUser.setLastname(newUser.getLastname());
        if (newUser.getEmail() != null) existingUser.setEmail(newUser.getEmail());
        if (newUser.getPassword() != null) existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        return new UserDto(userRepository.save(existingUser));
    }

    @Transactional
    public void deleteUser(Long id, User user) {
        if (user.getUserRole().equals(UserRole.ADMIN)) {
        }
        else if (!user.getId().equals(id)) {
            throw new AccessDeniedException("You can only delete your own account");
        }
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException();
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto updateProfile(User user, UpdateProfileRequest request) {
        if (request.firstname() == null || request.firstname().isBlank() ||
                request.lastname() == null || request.lastname().isBlank()) {
            throw new IllegalArgumentException("Firstname and lastname are required");
        }

        user.setFirstname(request.firstname().trim());
        user.setLastname(request.lastname().trim());

        var saved = userRepository.save(user);
        return new UserDto(saved);
    }

    @Transactional
    public void changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (request.newPassword().isBlank()) {
            throw new IllegalArgumentException("New password cannot be blank");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(User user, DeleteAccountRequest request) {
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Password confirmation failed");
        }

        List<Tournament> organizedTournaments = tournamentRepository.findByOrganizer(user);
        organizedTournaments.forEach(tournament ->
                tournamentService.deleteTournament(tournament.getId(), user));

        teamRepository.findByCoach_Id(user.getId())
                .forEach(team -> {
                    team.setCoach(null);
                    teamRepository.save(team);
                });

        matchRepository.findByRefereeId(user.getId())
                .forEach(match -> {
                    match.setReferee(null);
                    matchRepository.save(match);
                });

        tournamentRepository.findDistinctByReferees_Id(user.getId())
                .forEach(tournament -> {
                    tournament.getReferees().removeIf(ref -> ref.getId().equals(user.getId()));
                    tournamentRepository.save(tournament);
                });

        tournamentRepository.findDistinctByFollowers_Id(user.getId())
                .forEach(tournament -> {
                    tournament.getFollowers().removeIf(follower -> follower.getId().equals(user.getId()));
                    tournamentRepository.save(tournament);
                });

        userRepository.delete(user);
    }
}
