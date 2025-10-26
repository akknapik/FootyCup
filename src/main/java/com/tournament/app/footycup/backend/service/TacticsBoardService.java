package com.tournament.app.footycup.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournament.app.footycup.backend.dto.tactics.TacticsBoardResponse;
import com.tournament.app.footycup.backend.dto.tactics.TacticsBoardStateRequest;
import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.TacticsBoard;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import com.tournament.app.footycup.backend.repository.TacticsBoardRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TacticsBoardService {

    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final TacticsBoardRepository tacticsBoardRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public TacticsBoardResponse getBoard(Long tournamentId, Long matchId, User organizer) {
        Match match = getAuthorizedMatch(tournamentId, matchId, organizer);
        return tacticsBoardRepository.findByMatch(match)
                .map(this::toResponse)
                .orElseGet(() -> emptyResponse(match));
    }

    @Transactional
    public TacticsBoardResponse saveBoard(Long tournamentId, Long matchId, User organizer, TacticsBoardStateRequest state) {
        Match match = getAuthorizedMatch(tournamentId, matchId, organizer);
        TacticsBoard board = tacticsBoardRepository.findByMatch(match)
                .orElseGet(() -> {
                    TacticsBoard created = new TacticsBoard();
                    created.setMatch(match);
                    return created;
                });

        TacticsBoardStateRequest sanitized = sanitizeState(state);

        board.setStateJson(serializeState(sanitized));
        board.setUpdatedAt(Instant.now());

        TacticsBoard saved = tacticsBoardRepository.save(board);
        return toResponse(saved);
    }

    @Transactional
    public void deleteBoard(Long tournamentId, Long matchId, User organizer) {
        Match match = getAuthorizedMatch(tournamentId, matchId, organizer);
        tacticsBoardRepository.findByMatch(match).ifPresent(tacticsBoardRepository::delete);
    }

    private Match getAuthorizedMatch(Long tournamentId, Long matchId, User organizer) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if (!tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
        if (match.getTournament() == null || !match.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Match does not belong to tournament");
        }
        return match;
    }

    private TacticsBoardStateRequest parseState(String json) {
        try {
            return objectMapper.readValue(json, TacticsBoardStateRequest.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to parse tactics board state", e);
        }
    }

    private String serializeState(TacticsBoardStateRequest state) {
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize tactics board state", e);
        }
    }

    private TacticsBoardStateRequest sanitizeState(TacticsBoardStateRequest state) {
        Instant lastUpdated = state.lastUpdated() != null ? state.lastUpdated() : Instant.now();
        return new TacticsBoardStateRequest(state.layers(), state.activeLayerId(), lastUpdated);
    }

    private TacticsBoardResponse toResponse(TacticsBoard board) {
        TacticsBoardStateRequest state = parseState(board.getStateJson());
        return new TacticsBoardResponse(
                board.getId(),
                board.getMatch().getId(),
                state.layers(),
                state.activeLayerId(),
                state.lastUpdated(),
                board.getUpdatedAt()
        );
    }

    private TacticsBoardResponse emptyResponse(Match match) {
        return new TacticsBoardResponse(
                null,
                match.getId(),
                List.of(),
                null,
                Instant.now(),
                null
        );
    }
}