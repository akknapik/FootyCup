package com.tournament.app.footycup.backend.controller;

import com.tournament.app.footycup.backend.model.BracketNode;
import com.tournament.app.footycup.backend.model.Group;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.service.FormatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("tournament/{tournamentId}/format")
public class FormatController {
    private final FormatService formatService;

    @GetMapping
    public ResponseEntity<Boolean> formatExits(@PathVariable Long tournamentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean exists = formatService.structureExists(tournamentId, user);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/group")
    public ResponseEntity<Void> generateGroupFormat(@PathVariable Long tournamentId,
                                                    @RequestParam int groupCount,
                                                    @RequestParam int teamsPerGroup,
                                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.generateGroupStructure(tournamentId, groupCount, teamsPerGroup, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bracket")
    public ResponseEntity<Void> generateBracketFormat(@PathVariable Long tournamentId,
                                                      @RequestParam int totalTeams,
                                                      Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.generateBracketStructure(tournamentId, totalTeams, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mixed")
    public ResponseEntity<Void> generateMixedFormat(@PathVariable Long tournamentId,
                                                    @RequestParam int groupCount,
                                                    @RequestParam int teamsPerGroup,
                                                    @RequestParam int advancing,
                                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.generateMixedStructure(tournamentId, groupCount, teamsPerGroup, advancing, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{slotId}/assign/{teamId}")
    public ResponseEntity<Void> assignTeam(@PathVariable Long tournamentId, @PathVariable Long slotId,
                                           @PathVariable Long teamId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.assignTeamToSlot(tournamentId, slotId, teamId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign-random")
    public ResponseEntity<Void> assignTeamsRandomly(@PathVariable Long tournamentId,
                                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.assignTeamsRandomlyToGroups(tournamentId, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/bracket/{nodeId}")
    public ResponseEntity<Void> assignTeamToNode(@PathVariable Long tournamentId,
                                                 @PathVariable Long nodeId,
                                                 @RequestParam Long teamId,
                                                 @RequestParam boolean homeTeam,
                                                 Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.assignTeamToNode(tournamentId, nodeId, teamId, homeTeam, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/groups")
    public ResponseEntity<List<Group>> getGroups(@PathVariable Long tournamentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Group> groups = formatService.getGroups(tournamentId, user);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/bracket")
    public ResponseEntity<List<BracketNode>> getBracket(@PathVariable Long tournamentId,
                                                        Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<BracketNode> bracketNodes = formatService.getBracketNodes(tournamentId, user);
        return ResponseEntity.ok(bracketNodes);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllStructures(@PathVariable Long tournamentId,
                                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        formatService.deleteAllStructures(tournamentId, user);
        return ResponseEntity.noContent().build();
    }
}
