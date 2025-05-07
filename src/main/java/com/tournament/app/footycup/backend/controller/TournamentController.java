package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.TournamentService;
import com.tournament.app.footycup.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final UserService userService;

    public TournamentController(TournamentService tournamentService, UserService userService) {
        this.tournamentService = tournamentService;
        this.userService = userService;
    }

    @Operation(summary = "Get tournaments created by the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournaments retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tournament.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing authentication", content = @Content),
            @ApiResponse(responseCode = "404", description = "No tournaments found", content = @Content)
    })
    @GetMapping("/my")
    public ResponseEntity<List<Tournament>> getMyTournaments(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = (User) authentication.getPrincipal();
        try {
            List<Tournament> myTournaments = tournamentService.getTournamentsByOrganizer(user);
            return ResponseEntity.ok(myTournaments);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Create a new tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournament created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tournament.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or authentication", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Tournament> createTournament(
            @RequestBody @Parameter(description = "Tournament data") Tournament request,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User organizer = (User) authentication.getPrincipal();
        Tournament tournament = tournamentService.createTournament(request, organizer);
        return ResponseEntity.ok(tournament);
    }

    @Operation(summary = "Get a tournament by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournament retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tournament.class))),
            @ApiResponse(responseCode = "400", description = "Invalid authentication", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tournament not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getById(
            @Parameter(description = "ID of the tournament") @PathVariable Long id,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = (User) authentication.getPrincipal();
        Tournament tournament = tournamentService.getTournamentById(id, user);
        return ResponseEntity.ok(tournament);
    }

    @Operation(summary = "Update an existing tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tournament updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Tournament.class))),
            @ApiResponse(responseCode = "400", description = "Invalid authentication or request data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tournament not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(
            @Parameter(description = "ID of the tournament") @PathVariable Long id,
            @RequestBody @Parameter(description = "Updated tournament data") Tournament updatedData,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = (User) authentication.getPrincipal();
        Tournament updated = tournamentService.updateTournament(id, updatedData, user);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a tournament")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tournament deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid authentication", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tournament not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(
            @Parameter(description = "ID of the tournament") @PathVariable Long id,
            Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }
        User user = (User) authentication.getPrincipal();
        tournamentService.deleteTournament(id, user);
        return ResponseEntity.noContent().build();
    }
}
