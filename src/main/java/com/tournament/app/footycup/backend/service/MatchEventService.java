package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.match.CreateMatchEventRequest;
import com.tournament.app.footycup.backend.dto.match.MatchStatisticsResponse;
import com.tournament.app.footycup.backend.dto.match.TeamMatchStatistics;
import com.tournament.app.footycup.backend.enums.MatchEventType;
import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.MatchEvent;
import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.MatchEventRepository;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import com.tournament.app.footycup.backend.repository.PlayerRepository;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class MatchEventService {

    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final FormatService formatService;

    @Transactional(readOnly = true)
    public List<MatchEvent> getEvents(Long tournamentId, Long matchId, User user) {
        var match = resolveAuthorizedMatch(tournamentId, matchId, user);
        return matchEventRepository.findByMatchIdOrderByMinuteDesc(match.getId());
    }

    @Transactional(readOnly = true)
    public MatchStatisticsResponse getStatistics(Long tournamentId, Long matchId, User user) {
        var match = resolveAuthorizedMatch(tournamentId, matchId, user);
        var events = matchEventRepository.findByMatchIdOrderByMinuteDesc(match.getId());

        var homeStats = buildTeamStatistics(match.getTeamHome(), events);
        var awayStats = buildTeamStatistics(match.getTeamAway(), events);

        return new MatchStatisticsResponse(homeStats, awayStats);
    }

    @Transactional
    public MatchEvent addEvent(Long tournamentId, Long matchId, CreateMatchEventRequest request, User user) {
        var match = resolveAuthorizedMatch(tournamentId, matchId, user);

        if (request.eventType() == null) {
            throw new IllegalArgumentException("Event type must be provided");
        }
        if (request.minute() == null || request.minute() < 0) {
            throw new IllegalArgumentException("Minute must be a non-negative value");
        }

        var eventType = MatchEventType.valueOf(request.eventType());
        var team = resolveTeam(match, request.teamId());
        var player = resolvePlayer(request.playerId(), team);
        var secondaryPlayer = resolvePlayer(request.secondaryPlayerId(), team);
        var description = normalizeDescription(request.description());

        var event = new MatchEvent();
        event.setMatch(match);
        event.setEventType(eventType);
        event.setMinute(request.minute());
        event.setTeam(team);
        event.setPlayer(player);
        event.setSecondaryPlayer(eventType == MatchEventType.SUBSTITUTION ? secondaryPlayer : null);
        event.setDescription(eventType == MatchEventType.OTHER ? description : null);
        event.setRecordedBy(user);

        var saved = matchEventRepository.save(event);
        updateMatchScores(match);
        return saved;
    }

    @Transactional
    public void deleteEvent(Long tournamentId, Long matchId, Long eventId, User user) {
        var match = resolveAuthorizedMatch(tournamentId, matchId, user);
        var event = matchEventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Match event not found"));

        if (!event.getMatch().getId().equals(match.getId())) {
            throw new IllegalArgumentException("Event does not belong to match");
        }

        matchEventRepository.delete(event);
        updateMatchScores(match);
    }

    private Match resolveAuthorizedMatch(Long tournamentId, Long matchId, User user) {
        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));

        if (match.getTournament() == null || !match.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Match does not belong to tournament");
        }

        var organizer = match.getTournament().getOrganizer();
        boolean isOrganizer = organizer != null && organizer.getId().equals(user.getId());
        boolean isReferee = match.getReferee() != null && match.getReferee().getId().equals(user.getId());

        if (!isOrganizer && !isReferee) {
            throw new AccessDeniedException("Insufficient permissions to manage match events");
        }

        return match;
    }

    private Team resolveTeam(Match match, Long teamId) {
        if (teamId == null) {
            throw new IllegalArgumentException("Team must be provided for the event");
        }

        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found"));

        boolean isHomeTeam = match.getTeamHome() != null && match.getTeamHome().getId().equals(teamId);
        boolean isAwayTeam = match.getTeamAway() != null && match.getTeamAway().getId().equals(teamId);

        if (!isHomeTeam && !isAwayTeam) {
            throw new IllegalArgumentException("Team is not assigned to this match");
        }

        return team;
    }

    private Player resolvePlayer(Long playerId, Team team) {
        if (playerId == null) {
            return null;
        }

        var player = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found"));

        if (player.getTeam() == null || !player.getTeam().getId().equals(team.getId())) {
            throw new IllegalArgumentException("Player does not belong to the selected team");
        }

        return player;
    }

    private void validateEventSpecificData(MatchEventType eventType, Player player, Player secondaryPlayer, String description) {
        if (eventType == MatchEventType.SUBSTITUTION) {
            if (player == null || secondaryPlayer == null) {
                throw new IllegalArgumentException("Both entering and leaving players must be selected for a substitution");
            }
            if (player.getId().equals(secondaryPlayer.getId())) {
                throw new IllegalArgumentException("Entering and leaving players must be different");
            }
        }

        if (eventType != MatchEventType.SUBSTITUTION && secondaryPlayer != null) {
            throw new IllegalArgumentException("Secondary player is only allowed for substitution events");
        }

        if (eventType != MatchEventType.OTHER && StringUtils.hasText(description)) {
            throw new IllegalArgumentException("Description is only allowed for other events");
        }
    }

    private String normalizeDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return null;
        }
        return description.trim();
    }

    private TeamMatchStatistics buildTeamStatistics(Team team, List<MatchEvent> events) {
        if (team == null) {
            return null;
        }

        long goals = 0L;
        long yellowCards = 0L;
        long redCards = 0L;
        long substitutions = 0L;
        long otherEvents = 0L;

        for (MatchEvent event : events) {
            if (event.getTeam() == null || !team.getId().equals(event.getTeam().getId())) {
                continue;
            }

            if (event.getEventType() == MatchEventType.GOAL) {
                goals++;
            } else if (event.getEventType() == MatchEventType.YELLOW_CARD) {
                yellowCards++;
            } else if (event.getEventType() == MatchEventType.RED_CARD) {
                redCards++;
            } else if (event.getEventType() == MatchEventType.SUBSTITUTION) {
                substitutions++;
            } else if (event.getEventType() == MatchEventType.OTHER) {
                otherEvents++;
            }
        }

        long total = goals + yellowCards + redCards + substitutions + otherEvents;

        return new TeamMatchStatistics(
                team.getId(),
                team.getName(),
                goals,
                yellowCards,
                redCards,
                substitutions,
                otherEvents,
                total
        );
    }

    private void updateMatchScores(Match match) {
        int homeGoals = 0;
        int awayGoals = 0;

        var events = matchEventRepository.findByMatchIdOrderByMinuteDesc(match.getId());
        for (MatchEvent e : events) {
            if (e.getEventType() != MatchEventType.GOAL || e.getTeam() == null) {
                continue;
            }

            if (match.getTeamHome() != null && e.getTeam().getId().equals(match.getTeamHome().getId())) {
                homeGoals++;
            } else if (match.getTeamAway() != null && e.getTeam().getId().equals(match.getTeamAway().getId())) {
                awayGoals++;
            }
        }

        match.setHomeScore(homeGoals);
        match.setAwayScore(awayGoals);
        matchRepository.save(match);
        formatService.recomputeStandingsForMatch(match);
    }
}