package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.UserRepository;
import com.tournament.app.footycup.backend.requests.AuthResponse;
import com.tournament.app.footycup.backend.requests.LoginRequest;
import com.tournament.app.footycup.backend.requests.RegistrationRequest;
import com.tournament.app.footycup.backend.service.EmailProducer;
import com.tournament.app.footycup.backend.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
@AllArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final EmailProducer emailProducer;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Email already in use",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Registration failed",
                    content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration request body",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegistrationRequest.class))
            ) RegistrationRequest request) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already in use");
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());

            userRepository.save(user);

            String subject = "Welcome in FootyCup!";
            String content = "Hi " + user.getFirstname() + ", thank you for registering in our system!";
            emailProducer.sendEmail(user.getEmail(), subject, content);

            return ResponseEntity.ok("User registered");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication failed", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ) LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String token = tokenService.generateToken(request.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
