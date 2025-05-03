package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.enums.MatchStatus;
import com.tournament.app.footycup.backend.model.*;
import com.tournament.app.footycup.backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class StructureGenerationService{
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final GroupRepository groupRepository;
    private final GroupTeamRepository groupTeamRepository;
    private final BracketNodeRepository bracketNodeRepository;
    private final MatchRepository matchRepository;

    public void generateGroupStructure(Long tournamentId, int groupCount, int teamsPerGroup, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        for (int g = 0; g < groupCount; g++) {
            Group group = new Group();
            group.setName("Group " + (char) ('A' + g));
            group.setTournament(tournament);
            groupRepository.save(group);

            for (int i = 0; i < teamsPerGroup; i++) {
                GroupTeam slot = new GroupTeam();
                slot.setGroup(group);
                slot.setTeam(null);
                slot.setPosition(i + 1);
                groupTeamRepository.save(slot);
            }
        }
    }

    public void generateBracketStructure(Long tournamentId, int totalTeams, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        int round = 1;
        int matchCount = totalTeams/2;
        int position = 0;

        List<BracketNode> previousRoundNodes = new ArrayList<>();
        while (matchCount > 0) {
            List<BracketNode> currentRoundNodes = new ArrayList<>();

            for (int i = 0; i < matchCount; i++) {
                BracketNode node = new BracketNode();
                node.setTournament(tournament);
                node.setRound(round);
                node.setPosition(++position);

                Match match = new Match();
                match.setTournament(tournament);
                match.setStatus(MatchStatus.SCHEDULED);

                matchRepository.save(match);
                node.setMatch(match);

                if(round > 1) {
                    node.setParentHomeNode(previousRoundNodes.get(i * 2));
                    node.setParentAwayNode(previousRoundNodes.get(i * 2 + 1));
                }

                bracketNodeRepository.save(node);
                currentRoundNodes.add(node);
            }

            previousRoundNodes = currentRoundNodes;
            round++;
            matchCount /= 2;
        }
    }

    public void generateMixedStructure(Long tournamentId, int groupCount, int teamsPerGroup, int advancing, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        generateGroupStructure(tournamentId, groupCount, teamsPerGroup, user);
        generateBracketStructure(tournamentId, advancing, user);
    }

    public void assignTeamsRandomlyToGroups(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        List<Group> groups = groupRepository.findByTournamentId(tournament.getId());
        List<Team> teams = new ArrayList<>(teamRepository.findByTournamentId(tournament.getId()));
        Collections.shuffle(teams);

        List<GroupTeam> emptySlots = groupTeamRepository.findByGroupTournamentId(tournament.getId())
                .stream()
                .filter(gt -> gt.getTeam() == null)
                .toList();

        if(teams.size() < emptySlots.size()) {
            throw new IllegalStateException("Too few teams");
        }

        for (int i = 0; i < emptySlots.size() && i < teams.size(); i++) {
            GroupTeam slot = emptySlots.get(i);
            slot.setTeam(teams.get(i));
            groupTeamRepository.save(slot);
        }
    }
}
