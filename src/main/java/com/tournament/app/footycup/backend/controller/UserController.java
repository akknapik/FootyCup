package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.UserDto;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.UserRepository;
import com.tournament.app.footycup.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") long id) {
        UserDto user = userService.getUserById(id);

        if(user==null) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error","User not found"));
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserDto> addUser(@RequestBody User user) {
        UserDto newUser = userService.addUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            UserDto updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }
}
