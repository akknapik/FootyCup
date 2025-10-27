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
    public ResponseEntity<MyTournamentsResponse> getMyTournaments(@AuthenticationPrincipal User organizer) {
        var organized = tournamentService.getTournamentsByOrganizer(organizer)
                .stream().map(tournamentMapper::toItem).toList();
        var refereeing = tournamentService.getTournamentsAsReferee(organizer)
                .stream().map(tournamentMapper::toItem).toList();
        var coaching = tournamentService.getTournamentsAsCoach(organizer)
                .stream().map(tournamentMapper::toItem).toList();
        var observing = tournamentService.getFollowedTournaments(organizer)
                .stream().map(t -> tournamentMapper.toItem(t, true)).toList();
        return  ResponseEntity.ok(new MyTournamentsResponse(organized, refereeing, coaching, observing));
    }

    @GetMapping("/public")
    public ResponseEntity<List<TournamentItemResponse>> getPublicTournaments() {
        var tournaments = tournamentService.getPublicTournaments();
        var dto = tournaments.stream().map(tournamentMapper::toItem).toList();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(
            @RequestBody @Valid CreateTournamentRequest request,
            @AuthenticationPrincipal User organizer) {
        var saved = tournamentService.createTournament(request, organizer);
        var body = tournamentMapper.toResponse(saved);
        var location = UriComponentsBuilder.fromPath("/tournaments/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<TournamentResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User organizer) {
        var tournament = tournamentService.getTournamentById(id, organizer);
        var followed = tournamentService.isFollowing(tournament, organizer);
        return ResponseEntity.ok(tournamentMapper.toResponse(tournament, followed));
    }

    @GetMapping("/public/{id:\\d+}")
    public ResponseEntity<TournamentResponse> getPublicById(@PathVariable Long id) {
        var tournament = tournamentService.getTournamentById(id, null);
        return ResponseEntity.ok(tournamentMapper.toResponse(tournament));
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTournamentRequest updatedData,
            @AuthenticationPrincipal User organizer) {
        var updated = tournamentService.updateTournament(id, updatedData, organizer);
        return ResponseEntity.ok(tournamentMapper.toResponse(updated));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteTournament(
            @PathVariable Long id,
            @AuthenticationPrincipal User organizer) {
        tournamentService.deleteTournament(id, organizer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id:\\d+}/referees")
    public ResponseEntity<List<UserRef>> getReferees(
            @PathVariable Long id,
            @AuthenticationPrincipal User organizer) {
        var refs = tournamentService.getReferees(id, organizer);
        var dto = refs.stream().map(commonMapper::toUserRef).toList();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{id:\\d+}/referees")
    public ResponseEntity<List<UserRef>> addReferee(
            @PathVariable Long id,
            @RequestBody @Valid AddRefereeRequest addRefereeRequest,
            @AuthenticationPrincipal User organizer) {
        var refs = tournamentService.addReferee(id, addRefereeRequest.email(), organizer);
        var dto = refs.stream().map(commonMapper::toUserRef).toList();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id:\\d+}/referees/{refereeId:\\d+}")
    public ResponseEntity<Void> removeReferee(
            @PathVariable Long id,
            @PathVariable Long refereeId,
            @AuthenticationPrincipal User organizer) {
        tournamentService.removeReferee(id, refereeId, organizer);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id:\\d+}/follow")
    public ResponseEntity<Void> followTournament(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        tournamentService.followTournament(id, user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id:\\d+}/follow")
    public ResponseEntity<Void> unfollowTournament(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        tournamentService.unfollowTournament(id, user);
        return ResponseEntity.noContent().build();
    }
}
