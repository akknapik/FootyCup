package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.UpdateMatchResultRequest;
import com.tournament.app.footycup.backend.enums.MatchStatus;
import com.tournament.app.footycup.backend.model.*;
import com.tournament.app.footycup.backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class MatchService {
    private final GroupRepository groupRepository;
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final BracketNodeRepository bracketNodeRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    @Transactional(readOnly = true)
    public List<Match> getMatches(Long tournamentId, User user) {
        var tournament = resolveTournament(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);
        return matchRepository.findByTournamentId(tournamentId);
    }

    @Transactional(readOnly = true)
    public Match getMatch(Long tournamentId, Long matchId, User user) {
        var tournament = resolveTournament(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);

        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
        if (match.getTournament() == null || !match.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Match does not belong to tournament");
        }
        authorizationService.ensureCanViewMatch(match, user);
        return match;
    }

    @Transactional(readOnly = true)
    public List<Match> getGroupMatches(Long tournamentId, Long groupId, User user) {
        var tournament = resolveTournament(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);

        var group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        if (group.getTournament() == null || !group.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Group does not belong to tournament");
        }
        return matchRepository.findByGroupId(group.getId());
    }

    @Transactional
    public void generateGroupMatches(Long tournamentId, User organizer) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);

        var groups = groupRepository.findByTournamentId(tournamentId);

        for (var group : groups) {
            var teams = group.getGroupTeams().stream()
                    .map(GroupTeam::getTeam)
                    .filter(Objects::nonNull)
                    .toList();

            int matchCounter = 1;
            String groupPrefix = group.getName().replace("Group", "");

            for (int i = 0; i < teams.size(); i++) {
                for (int j = i + 1; j < teams.size(); j++) {
                    var home = teams.get(i);
                    var away = teams.get(j);

                    var match = new Match();
                    match.setTournament(tournament);
                    match.setGroup(group);
                    match.setTeamHome(home);
                    match.setTeamAway(away);
                    match.setStatus(MatchStatus.NOT_SCHEDULED);
                    match.setName(groupPrefix + matchCounter++);
                    matchRepository.save(match);
                }
            }
        }
    }

    @Transactional
    public void deleteMatch(Long tournamentId, Long matchId, User organizer) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);

        var match = getMatch(tournamentId, matchId, organizer);
        matchRepository.delete(match);
    }

    @Transactional
    public void deleteAllMatches(Long tournamentId, User organizer) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);

        var matches = getMatches(tournamentId, organizer);
        matchRepository.deleteAll(matches);
    }

    @Transactional
    public void updateSingleMatchResult(Long tournamentId, Long matchId, UpdateMatchResultRequest updated, User organizer) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);

        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found"));

        if (!match.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Match does not belong to tournament");
        }

        match.setHomeScore(updated.homeScore());
        match.setAwayScore(updated.awayScore());

        matchRepository.save(match);
        propagateWinner(match);
    }

    @Transactional
    public void propagateWinner(Match finishedMatch) {
        var winner = determineWinner(finishedMatch);
        if (winner == null) {
            return;
        }

        var currentNode = bracketNodeRepository.findByMatch(finishedMatch);
        if (currentNode == null) {
            return;
        }

        var nextNodes = bracketNodeRepository.findByParentHomeNodeOrParentAwayNode(currentNode, currentNode);

        for (BracketNode next : nextNodes) {
            var m = next.getMatch();

            if (currentNode.equals(next.getParentHomeNode())) {
                m.setTeamHome(winner);
            }
            if (currentNode.equals(next.getParentAwayNode())) {
                m.setTeamAway(winner);
            }

            matchRepository.save(m);
        }
    }

    private Team determineWinner(Match finishedMatch) {
        if(finishedMatch.getHomeScore() > finishedMatch.getAwayScore()) {
            return finishedMatch.getTeamHome();
        } else if (finishedMatch.getAwayScore() > finishedMatch.getHomeScore()) {
            return finishedMatch.getTeamAway();
        }
        else {
            return null;
        }
    }

    @Transactional
    public Match assignReferee(Long tournamentId, Long matchId, Long refereeId, User organizer) {
        var tournament = tournamentRepository.findWithRefereesById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        authorizationService.ensureOrganizer(tournament, organizer);

        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));

        if (match.getTournament() == null || !match.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Match does not belong to tournament");
        }

        boolean refereeAssigned = tournament.getReferees().stream()
                .anyMatch(ref -> Objects.equals(ref.getId(), refereeId));
        if (!refereeAssigned) {
            throw new IllegalArgumentException("Referee not assigned to tournament");
        }

        var resolvedReferee = userRepository.findById(refereeId)
                .orElseThrow(() -> new NoSuchElementException("Referee not found"));

        match.setReferee(resolvedReferee);
        var saved = matchRepository.save(match);
        saved.setReferee(resolvedReferee);
        return saved;
    }

    private Tournament resolveTournament(Long tournamentId) {
        return tournamentRepository.findWithRefereesById(tournamentId)
                .orElseGet(() -> tournamentRepository.findById(tournamentId)
                        .orElseThrow(() -> new NoSuchElementException("Tournament not found")));
    }
}
