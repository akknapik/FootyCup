package com.tournament.app.footycup.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tournament.app.footycup.backend.dto.tactics.TacticsBoardResponse;
import com.tournament.app.footycup.backend.dto.tactics.TacticsBoardStateRequest;
import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.TacticsBoard;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import com.tournament.app.footycup.backend.repository.TacticsBoardRepository;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TacticsBoardService {

    private final MatchRepository matchRepository;
    private final TacticsBoardRepository tacticsBoardRepository;
    private final TeamRepository teamRepository;
    private final AuthorizationService authorizationService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public TacticsBoardResponse getBoard(Long tournamentId, Long matchId, User user, Long teamId) {
        Match match = resolveMatch(tournamentId, matchId);
        Team boardTeam = resolveTeamForBoard(match, user, teamId);
        var boardOpt = findBoard(match, boardTeam);
        return boardOpt.map(this::toResponse)
                .orElseGet(() -> emptyResponse(match, boardTeam));
    }

    @Transactional
    public TacticsBoardResponse saveBoard(Long tournamentId, Long matchId, User user, Long teamId,
                                          TacticsBoardStateRequest state) {
        Match match = resolveMatch(tournamentId, matchId);
        Team boardTeam = resolveTeamForBoard(match, user, teamId);
        ensureBoardWritePermissions(match, user, boardTeam);

        TacticsBoard board = findBoard(match, boardTeam)
                .orElseGet(() -> {
                    TacticsBoard created = new TacticsBoard();
                    created.setMatch(match);
                    created.setTeam(boardTeam);
                    return created;
                });

        TacticsBoardStateRequest sanitized = sanitizeState(state);

        board.setStateJson(serializeState(sanitized));
        board.setUpdatedAt(Instant.now());
        board.setTeam(boardTeam);

        TacticsBoard saved = tacticsBoardRepository.save(board);
        return toResponse(saved);
    }

    @Transactional
    public void deleteBoard(Long tournamentId, Long matchId, User user, Long teamId) {
        Match match = resolveMatch(tournamentId, matchId);
        Team boardTeam = resolveTeamForBoard(match, user, teamId);
        ensureBoardWritePermissions(match, user, boardTeam);
        findBoard(match, boardTeam).ifPresent(tacticsBoardRepository::delete);
    }

    private Match resolveMatch(Long tournamentId, Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
        if (match.getTournament() == null || !match.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Match does not belong to tournament");
        }
        return match;
    }

    private Team resolveTeamForBoard(Match match, User user, Long teamId) {
        var tournament = match.getTournament();
        if (authorizationService.isOrganizer(tournament, user)) {
            return teamId == null ? null : resolveMatchTeam(match, teamId);
        }

        if (authorizationService.isCoachOfTeam(match.getTeamHome(), user)) {
            if (teamId == null || match.getTeamHome().getId().equals(teamId)) {
                return match.getTeamHome();
            }
            throw new AccessDeniedException("Lack of authorization");
        }

        if (authorizationService.isCoachOfTeam(match.getTeamAway(), user)) {
            if (teamId == null || match.getTeamAway().getId().equals(teamId)) {
                return match.getTeamAway();
            }
            throw new AccessDeniedException("Lack of authorization");
        }

        throw new AccessDeniedException("Lack of authorization");
    }

    private void ensureBoardWritePermissions(Match match, User user, Team team) {
        if (team == null) {
            authorizationService.ensureOrganizer(match.getTournament(), user);
        } else {
            authorizationService.ensureCoachForMatchTeam(match, user, team);
        }
    }

    private Team resolveMatchTeam(Match match, Long teamId) {
        if (match.getTeamHome() != null && match.getTeamHome().getId().equals(teamId)) {
            return match.getTeamHome();
        }
        if (match.getTeamAway() != null && match.getTeamAway().getId().equals(teamId)) {
            return match.getTeamAway();
        }
        return teamRepository.findById(teamId)
                .filter(team -> team.getTournament() != null
                        && team.getTournament().getId().equals(match.getTournament().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Team is not part of this match"));
    }

    private java.util.Optional<TacticsBoard> findBoard(Match match, Team team) {
        return team == null
                ? tacticsBoardRepository.findByMatchAndTeamIsNull(match)
                : tacticsBoardRepository.findByMatchAndTeam(match, team);
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
                board.getTeam() != null ? board.getTeam().getId() : null,
                state.layers(),
                state.activeLayerId(),
                state.lastUpdated(),
                board.getUpdatedAt()
        );
    }

    private TacticsBoardResponse emptyResponse(Match match, Team team) {
        return new TacticsBoardResponse(
                null,
                match.getId(),
                team != null ? team.getId() : null,
                List.of(),
                null,
                Instant.now(),
                null
        );
    }
}