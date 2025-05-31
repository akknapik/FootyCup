package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.enums.MatchStatus;
import com.tournament.app.footycup.backend.model.*;
import com.tournament.app.footycup.backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class FormatService{
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final GroupRepository groupRepository;
    private final GroupTeamRepository groupTeamRepository;
    private final BracketNodeRepository bracketNodeRepository;
    private final MatchRepository matchRepository;
    private final MatchService matchService;

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
                match.setStatus(MatchStatus.NOT_SCHEDULED);
                match.setName(getBracketMatchName(round, totalTeams));

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

    public void assignTeamToSlot(Long tournamentId, Long slotId, Long teamId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Lack of authorization");
        }

        GroupTeam slot = groupTeamRepository.findById(slotId)
                .orElseThrow(() -> new NoSuchElementException("Slot not found"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found"));

        slot.setTeam(team);
        groupTeamRepository.save(slot);
    }

    public void assignTeamsRandomlyToGroups(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
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

    public void assignTeamToNode(Long tournamentId, Long nodeId, Long teamId, boolean homeTeam, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        BracketNode node = bracketNodeRepository.findById(nodeId)
                .orElseThrow(() -> new NoSuchElementException("Node not found"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found"));
        Match match = node.getMatch();
        if (homeTeam) {
            match.setTeamHome(team);
        } else {
            match.setTeamAway(team);
        }
        matchRepository.save(match);
    }


    public boolean structureExists(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        return !groupRepository.findByTournamentId(tournamentId).isEmpty()
                || !bracketNodeRepository.findByTournamentId(tournamentId).isEmpty();
    }

    public Group getGroup(Long tournamentId, Long groupId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        return group;
    }

    public List<Group> getGroups(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        List<Group> groups = groupRepository.findByTournamentId(tournamentId);
        return groups;
    }

    public BracketNode getBracketNode(Long tournamentId, Long bracketId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        BracketNode node = bracketNodeRepository.findById(bracketId)
                .orElseThrow(() -> new NoSuchElementException("BracketNode not found"));
        return node;
    }

    public List<BracketNode> getBracketNodes(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        List<BracketNode> bracketNodes = bracketNodeRepository.findByTournamentId(tournamentId);
        return bracketNodes;
    }

    public void deleteGroup(Long tournamentId, Long groupId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        Group group = getGroup(tournamentId, groupId, user);
        groupRepository.delete(group);
    }

    public void deleteGroups(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        List<Group> groups = getGroups(tournamentId, user);
        for(Group group : groups) {
            List<Match> matches = matchService.getGroupMatches(tournamentId, user, group.getId());
            matchRepository.deleteAll(matches);
            groupRepository.delete(group);
        }
    }

    public void deleteBracket(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        List<BracketNode> bracketNodes = getBracketNodes(tournamentId, user);
        bracketNodeRepository.deleteAll(bracketNodes);
    }

    public void deleteAllStructures(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }

        deleteGroups(tournamentId, user);
        deleteBracket(tournamentId, user);
    }

    public void propagateWinner(Match finishedMatch) {
        Team winner = determineWinner(finishedMatch);
        if(winner == null) {
            return;
        }

        BracketNode currentNode = bracketNodeRepository.findByMatch(finishedMatch);

        List<BracketNode> nextNodes = bracketNodeRepository.findByParentHomeNodeOrParentAwayNode(currentNode,
                currentNode);

        for(BracketNode next : nextNodes) {
            Match m = next.getMatch();

            if(currentNode.equals(next.getParentHomeNode())) {
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

    private String getBracketMatchName(int round, int totalTeams) {
        int totalRounds = (int) (Math.log(totalTeams) / Math.log(2));
        int currentStage = totalRounds - round + 1;

        return switch (totalTeams) {
            case 4 -> switch (currentStage) {
                case 1 -> "Final";
                case 2 -> "Semi-Final";
                default -> "Round " + currentStage;
            };
            case 8 -> switch (currentStage) {
                case 1 -> "Final";
                case 2 -> "Semi-Final";
                case 3 -> "Quarter-Final";
                default -> "Round " + currentStage;
            };
            case 16 -> switch (currentStage) {
                case 1 -> "Final";
                case 2 -> "Semi-Final";
                case 3 -> "Quarter-Final";
                case 4 -> "1/8 Final";
                default -> "Round " + currentStage;
            };
            case 32 -> switch (currentStage) {
                case 1 -> "Final";
                case 2 -> "Semi-Final";
                case 3 -> "Quarter-Final";
                case 4 -> "1/8 Final";
                case 5 -> "1/16 Final";
                default -> "Round " + currentStage;
            };
            case 64 -> switch (currentStage) {
                case 1 -> "Final";
                case 2 -> "Semi-Final";
                case 3 -> "Quarter-Final";
                case 4 -> "1/8 Final";
                case 5 -> "1/16 Final";
                case 6 -> "1/32 Final";
                default -> "Round " + currentStage;
            };
            default -> "Round " + currentStage;
        };
    }

}
