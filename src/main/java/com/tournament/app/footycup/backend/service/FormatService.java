package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.format.GenerateMixRequest;
import com.tournament.app.footycup.backend.dto.format.bracket.AssignTeamToNodeRequest;
import com.tournament.app.footycup.backend.dto.format.bracket.GenerateBracketRequest;
import com.tournament.app.footycup.backend.dto.format.group.AssignTeamToSlotRequest;
import com.tournament.app.footycup.backend.dto.format.group.GenerateGroupRequest;
import com.tournament.app.footycup.backend.enums.MatchStatus;
import com.tournament.app.footycup.backend.model.*;
import com.tournament.app.footycup.backend.repository.*;
import lombok.AllArgsConstructor;
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
    private final AuthorizationService authorizationService;

    public void generateGroupStructure(Long tournamentId, GenerateGroupRequest request, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);


        for (int g = 0; g < request.groupCount(); g++) {
            Group group = new Group();
            group.setName("Group " + (char) ('A' + g));
            group.setTournament(tournament);
            groupRepository.save(group);

            for (int i = 0; i < request.teamsPerGroup(); i++) {
                GroupTeam slot = new GroupTeam();
                slot.setGroup(group);
                slot.setTeam(null);
                slot.setPosition(i + 1);
                groupTeamRepository.save(slot);
            }
        }
    }

    public void generateBracketStructure(Long tournamentId, GenerateBracketRequest request, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

        int round = 1;
        int matchCount = request.totalTeams()/2;
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
                match.setName(getBracketMatchName(round, request.totalTeams()));

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

    public void generateMixedStructure(Long tournamentId, GenerateMixRequest request, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

        generateGroupStructure(tournamentId, new GenerateGroupRequest(request.groupCount(), request.teamsPerGroup()),
                organizer);
        generateBracketStructure(tournamentId, new GenerateBracketRequest(request.advancing()), organizer);
    }

    public void assignTeamToSlot(Long tournamentId, AssignTeamToSlotRequest request, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

        GroupTeam slot = groupTeamRepository.findById(request.slotId())
                .orElseThrow(() -> new NoSuchElementException("Slot not found"));
        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new NoSuchElementException("Team not found"));

        slot.setTeam(team);
        groupTeamRepository.save(slot);
    }

    public void assignTeamsRandomlyToGroups(Long tournamentId, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

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

    public void assignTeamToNode(Long tournamentId, AssignTeamToNodeRequest request, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

        BracketNode node = bracketNodeRepository.findById(request.nodeId())
                .orElseThrow(() -> new NoSuchElementException("Node not found"));
        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new NoSuchElementException("Team not found"));
        Match match = node.getMatch();
        if (request.homeTeam()) {
            match.setTeamHome(team);
        } else {
            match.setTeamAway(team);
        }
        matchRepository.save(match);
    }


    public boolean structureExists(Long tournamentId, User user) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);

        return !groupRepository.findByTournamentId(tournamentId).isEmpty()
                || !bracketNodeRepository.findByTournamentId(tournamentId).isEmpty();
    }

    public Group getGroup(Long tournamentId, Long groupId, User user) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        return group;
    }

    public List<Group> getGroups(Long tournamentId, User user) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);

        List<Group> groups = groupRepository.findByTournamentId(tournamentId);
        return groups;
    }

    public BracketNode getBracketNode(Long tournamentId, Long bracketId, User user) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);

        BracketNode node = bracketNodeRepository.findById(bracketId)
                .orElseThrow(() -> new NoSuchElementException("BracketNode not found"));
        return node;
    }

    public List<BracketNode> getBracketNodes(Long tournamentId, User user) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);

        List<BracketNode> bracketNodes = bracketNodeRepository.findByTournamentId(tournamentId);
        return bracketNodes;
    }

    public void deleteGroup(Long tournamentId, Long groupId, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

        Group group = getGroup(tournamentId, groupId, organizer);
        groupRepository.delete(group);
    }

    public void deleteGroups(Long tournamentId, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

        List<Group> groups = getGroups(tournamentId, organizer);
        for(Group group : groups) {
            List<Match> matches = matchService.getGroupMatches(tournamentId, group.getId(), organizer);
            matchRepository.deleteAll(matches);
            groupRepository.delete(group);
        }
    }

    public void deleteBracket(Long tournamentId, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

        List<BracketNode> bracketNodes = getBracketNodes(tournamentId, organizer);
        bracketNodeRepository.deleteAll(bracketNodes);
    }

    public void deleteAllStructures(Long tournamentId, User organizer) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureOrganizer(tournament, organizer);

        deleteGroups(tournamentId, organizer);
        deleteBracket(tournamentId, organizer);
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

    public List<Group> getGroupsWithStandings(Long tournamentId, User user) {
        Tournament tournament = getTournamentOrThrow(tournamentId);
        authorizationService.ensureCanViewTournament(tournament, user);

        List<Group> groups = groupRepository.findByTournamentId(tournamentId);
        for (Group group : groups) {
            for (GroupTeam gt : group.getGroupTeams()) {
                computeStatsForGroupTeam(gt);
            }
            group.getGroupTeams().sort(Comparator.comparing(GroupTeam::getPoints).reversed());
        }

        return groups;
    }

    public void recomputeStandingsForMatch(Match match) {
        if (match == null || match.getGroup() == null) {
            return;
        }

        Group group = groupRepository.findById(match.getGroup().getId())
                .orElseThrow(() -> new NoSuchElementException("Group not found"));

        List<GroupTeam> groupTeams = groupTeamRepository.findByGroupId(group.getId());
        for (GroupTeam gt : groupTeams) {
            if (gt.getTeam() == null) {
                continue;
            }
            computeStatsForGroupTeam(gt);
            groupTeamRepository.save(gt);
        }
    }

    private void computeStatsForGroupTeam(GroupTeam gt) {
        List<Match> matches = matchRepository.findByTeamInGroup(gt.getTeam(), gt.getGroup().getId());

        int points = 0, goalsFor = 0, goalsAgainst = 0;

        for (Match m : matches) {
            if (m.getHomeScore() == null || m.getAwayScore() == null) continue;

            boolean isHome = m.getTeamHome().getId().equals(gt.getTeam().getId());
            int scored = isHome ? m.getHomeScore() : m.getAwayScore();
            int conceded = isHome ? m.getAwayScore() : m.getHomeScore();

            goalsFor += scored;
            goalsAgainst += conceded;

            if (scored > conceded) points += 3;
            else if (scored == conceded) points += 1;
        }

        gt.setGoalsFor(goalsFor);
        gt.setGoalsAgainst(goalsAgainst);
        gt.setPoints(points);
    }

    private Tournament getTournamentOrThrow(Long tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
    }
}