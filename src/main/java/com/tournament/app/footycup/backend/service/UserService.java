package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.UserDto;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto addUser(User user) {
        return new UserDto(userRepository.save(user));
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDto::new).collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException());

        return new UserDto(user);
    }

    public UserDto updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException());

        if (user.getFirstname() != null) existingUser.setFirstname(user.getFirstname());
        if (user.getLastname() != null) existingUser.setLastname(user.getLastname());
        if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null) existingUser.setPassword(user.getPassword());

        return new UserDto(userRepository.save(existingUser));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException();
        }
        userRepository.deleteById(id);
    }
}
