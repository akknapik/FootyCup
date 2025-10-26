package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.team.PlayerStatisticsResponse;
import com.tournament.app.footycup.backend.dto.team.TeamStatisticsResponse;
import com.tournament.app.footycup.backend.enums.MatchEventType;
import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.MatchEvent;
import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.MatchEventRepository;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class TeamStatisticsService {

    private final TeamService teamService;
    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;

    @Transactional(readOnly = true)
    public PlayerStatisticsResponse getPlayerStatistics(Long tournamentId, Long teamId, Long playerId, User user) {
        var team = teamService.getTeamById(tournamentId, teamId, user);
        var player = team.getPlayerList().stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Player not found on the team"));

        var matches = fetchTeamMatches(tournamentId, teamId);
        var events = matchEventRepository.findByMatch_Tournament_IdAndTeam_Id(tournamentId, teamId);

        var statsMap = buildPlayerStatistics(matches, events);
        var accumulator = statsMap.getOrDefault(playerId, new PlayerStatsAccumulator());

        return accumulator.toResponse(player.getId(), player.getName());
    }

    @Transactional(readOnly = true)
    public TeamStatisticsResponse getTeamStatistics(Long tournamentId, Long teamId, User user) {
        var team = teamService.getTeamById(tournamentId, teamId, user);

        var matches = fetchTeamMatches(tournamentId, teamId);
        var events = matchEventRepository.findByMatch_Tournament_IdAndTeam_Id(tournamentId, teamId);

        var statsMap = buildPlayerStatistics(matches, events);

        Integer goals = 0;
        Integer yellowCards = 0;
        Integer redCards = 0;
        Integer substitutions = 0;
        Integer otherEvents = 0;

        for (var accumulator : statsMap.values()) {
            goals += accumulator.goals;
            yellowCards += accumulator.yellowCards;
            redCards += accumulator.redCards;
            substitutions += accumulator.substitutions;
            otherEvents += accumulator.otherEvents;
        }

        Integer matchesPlayed = Math.toIntExact(matches.stream()
                .map(Match::getId)
                .filter(Objects::nonNull)
                .distinct()
                .count());

        Integer minutesPlayed = Math.toIntExact(matches.stream()
                .map(Match::getDurationInMin)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum());

        Integer totalEvents = goals + yellowCards + redCards + substitutions + otherEvents;

        return new TeamStatisticsResponse(
                team.getId(),
                team.getName(),
                matchesPlayed,
                minutesPlayed,
                goals,
                yellowCards,
                redCards,
                substitutions,
                otherEvents,
                totalEvents
        );
    }

    private List<Match> fetchTeamMatches(Long tournamentId, Long teamId) {
        return matchRepository.findByTournamentIdAndTeamId(tournamentId, teamId);
    }

    private Map<Long, PlayerStatsAccumulator> buildPlayerStatistics(List<Match> matches, List<MatchEvent> events) {
        Map<Long, Integer> matchDurations = new HashMap<>();
        for (Match match : matches) {
            if (match.getId() != null) {
                matchDurations.putIfAbsent(match.getId(), match.getDurationInMin());
            }
        }

        Map<Long, PlayerStatsAccumulator> stats = new HashMap<>();

        for (MatchEvent event : events) {
            Long matchId = event.getMatch() != null ? event.getMatch().getId() : null;
            Integer duration = matchId != null ? matchDurations.get(matchId) : null;

            Player primary = event.getPlayer();
            if (primary != null) {
                var accumulator = stats.computeIfAbsent(primary.getId(), id -> new PlayerStatsAccumulator());
                accumulator.registerMatch(matchId, duration);
                accumulator.recordPrimaryEvent(event.getEventType());
            }

            Player secondary = event.getSecondaryPlayer();
            if (secondary != null && event.getEventType() == MatchEventType.SUBSTITUTION) {
                var accumulator = stats.computeIfAbsent(secondary.getId(), id -> new PlayerStatsAccumulator());
                accumulator.registerMatch(matchId, duration);
            }
        }

        return stats;
    }

    private static class PlayerStatsAccumulator {
        private final Set<Long> matchIds = new HashSet<>();
        private Integer minutesPlayed = 0;
        private Integer goals = 0;
        private Integer yellowCards = 0;
        private Integer redCards = 0;
        private Integer substitutions = 0;
        private Integer otherEvents = 0;

        void registerMatch(Long matchId, Integer duration) {
            if (matchId == null || matchIds.contains(matchId)) {
                return;
            }
            matchIds.add(matchId);
            if (duration != null) {
                minutesPlayed += duration;
            }
        }

        void recordPrimaryEvent(MatchEventType type) {
            if (type == null) {
                return;
            }

            switch (type) {
                case GOAL -> goals++;
                case YELLOW_CARD -> yellowCards++;
                case RED_CARD -> redCards++;
                case SUBSTITUTION -> substitutions++;
                case OTHER -> otherEvents++;
            }
        }

        PlayerStatisticsResponse toResponse(Long playerId, String playerName) {
            Integer matchesPlayed = matchIds.size();
            Integer totalEvents = goals + yellowCards + redCards + substitutions + otherEvents;
            return new PlayerStatisticsResponse(
                    playerId,
                    playerName,
                    matchesPlayed,
                    minutesPlayed,
                    goals,
                    yellowCards,
                    redCards,
                    substitutions,
                    otherEvents,
                    totalEvents
            );
        }
    }
}
