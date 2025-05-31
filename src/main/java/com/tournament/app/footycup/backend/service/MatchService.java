package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.enums.MatchStatus;
import com.tournament.app.footycup.backend.model.*;
import com.tournament.app.footycup.backend.repository.GroupRepository;
import com.tournament.app.footycup.backend.repository.GroupTeamRepository;
import com.tournament.app.footycup.backend.repository.MatchRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class MatchService {
    private final GroupRepository groupRepository;
    private final GroupTeamRepository groupTeamRepository;
    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;

    public List<Match> getMatches(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        List<Match> matches = matchRepository.findByTournamentId(tournamentId);
        return matches;
    }

    public Match getMatch(Long tournamentId, Long matchId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new NoSuchElementException("Match not found"));
        return match;
    }

    public List<Match> getGroupMatches(Long tournamentId, User user, Long groupId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        List<Match> matches = matchRepository.findByGroupId(groupId);
        return matches;
    }

    public void generateGroupMatches(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        List<Group> groups = groupRepository.findByTournamentId(tournamentId);

        for (Group group : groups) {
            List<Team> teams = group.getGroupTeams().stream()
                    .map(GroupTeam::getTeam)
                    .filter(Objects::nonNull)
                    .toList();

            int matchCounter = 1;
            String groupPrefix = group.getName().replace("Group", "");

            for (int i = 0; i < teams.size(); i++) {
                for (int j = i + 1; j < teams.size(); j++) {
                    Team home = teams.get(i);
                    Team away = teams.get(j);

                    Match match = new Match();
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

    public void deleteMatch(Long tournamentId, Long matchId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        Match match = getMatch(tournamentId, matchId, user);
        matchRepository.delete(match);
    }

    public void deleteAllMatches(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        List<Match> matches = getMatches(tournamentId, user);
        matchRepository.deleteAll(matches);
    }
}
