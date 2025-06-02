package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.UserDto;
import com.tournament.app.footycup.backend.enums.UserRole;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

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

    public UserDto updateUser(Long id, User newUser, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());
        if(!user.getUserRole().equals(UserRole.ADMIN) || user.getId() != id) {
            throw new AccessDeniedException("Lack of authorization");
        }

        if (newUser.getFirstname() != null) existingUser.setFirstname(newUser.getFirstname());
        if (newUser.getLastname() != null) existingUser.setLastname(newUser.getLastname());
        if (newUser.getEmail() != null) existingUser.setEmail(newUser.getEmail());
        if (newUser.getPassword() != null) existingUser.setPassword(newUser.getPassword());

        return new UserDto(userRepository.save(existingUser));
    }

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
}
