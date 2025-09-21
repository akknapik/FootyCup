package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.dto.common.UserRef;
import com.tournament.app.footycup.backend.dto.tournament.*;
import com.tournament.app.footycup.backend.mapper.CommonMapper;
import com.tournament.app.footycup.backend.mapper.TournamentMapper;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.TournamentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;
    private final CommonMapper commonMapper;
    private final TournamentMapper tournamentMapper;

    @GetMapping("/my")
    public ResponseEntity<List<TournamentItemResponse>> getMyTournaments(@AuthenticationPrincipal User organizer) {
        var tournaments = tournamentService.getTournamentsByOrganizer(organizer);
        var dto = tournaments.stream().map(tournamentMapper::toItem).toList();
        return  ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(
            @RequestBody @Valid CreateTournamentRequest request,
            @AuthenticationPrincipal User organizer) {
        var saved = tournamentService.createTournament(request, organizer);
        var body = tournamentMapper.toResponse(saved, commonMapper);
        var location = UriComponentsBuilder.fromPath("/tournaments/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User organizer) {
        var tournament = tournamentService.getTournamentById(id, organizer);
        return ResponseEntity.ok(tournamentMapper.toResponse(tournament, commonMapper));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTournamentRequest updatedData,
            @AuthenticationPrincipal User organizer) {
        var updated = tournamentService.updateTournament(id, updatedData, organizer);
        return ResponseEntity.ok(tournamentMapper.toResponse(updated, commonMapper));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(
            @PathVariable Long id,
            @AuthenticationPrincipal User organizer) {
        tournamentService.deleteTournament(id, organizer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/referees")
    public ResponseEntity<List<UserRef>> getReferees(
            @PathVariable Long id,
            @AuthenticationPrincipal User organizer) {
        var refs = tournamentService.getReferees(id, organizer);
        var dto = refs.stream().map(commonMapper::toUserRef).toList();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id}/referees")
    public ResponseEntity<List<UserRef>> addReferee(
            @PathVariable Long id,
            @RequestBody @Valid AddRefereeRequest addRefereeRequest,
            @AuthenticationPrincipal User organizer) {
        var refs = tournamentService.addReferee(id, addRefereeRequest.email(), organizer);
        var dto = refs.stream().map(commonMapper::toUserRef).toList();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}/referees/{refereeId}")
    public ResponseEntity<Void> removeReferee(
            @PathVariable Long id,
            @PathVariable Long refereeId,
            @AuthenticationPrincipal User organizer) {
        tournamentService.removeReferee(id, refereeId, organizer);
        return ResponseEntity.noContent().build();
    }
}
