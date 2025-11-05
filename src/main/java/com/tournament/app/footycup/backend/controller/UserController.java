package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.UserDto;
import com.tournament.app.footycup.backend.dto.account.ChangePasswordRequest;
import com.tournament.app.footycup.backend.dto.account.DeleteAccountRequest;
import com.tournament.app.footycup.backend.dto.account.UpdateProfileRequest;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.UserRepository;
import com.tournament.app.footycup.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<UserDto> usersDto = userService.getAllUsers(user);
        return ResponseEntity.ok(usersDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserDto userDto = new UserDto(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getUserRole());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID") @PathVariable("id") Long id,
            @RequestBody @Parameter(description = "Updated user data") User newUser,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            UserDto updatedUser = userService.updateUser(id, newUser, user);
            return ResponseEntity.ok(updatedUser);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User ID") @PathVariable("id") Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            userService.deleteUser(id, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("message", "User deleted successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @PutMapping("/me/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            var updated = userService.updateProfile(user, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/me/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            userService.changePassword(user, request);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(@Valid @RequestBody DeleteAccountRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            userService.deleteAccount(user, request);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
