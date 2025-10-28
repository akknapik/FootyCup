package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.Match;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TeamRepository teamRepository;

    public boolean canViewTournament(Tournament tournament, User user) {
        if (tournament == null) {
            return false;
        }
        if (tournament.isPublicVisible()) {
            return true;
        }
        if (user == null) {
            return false;
        }
        if (isOrganizer(tournament, user)) {
            return true;
        }
        if (isReferee(tournament, user)) {
            return true;
        }
        return teamRepository.existsByTournamentIdAndCoach_Id(tournament.getId(), user.getId());
    }

    public void ensureCanViewTournament(Tournament tournament, User user) {
        if (!canViewTournament(tournament, user)) {
            throw new AccessDeniedException("Lack of authorization");
        }
    }

    public void ensureOrganizer(Tournament tournament, User user) {
        if (!isOrganizer(tournament, user)) {
            throw new AccessDeniedException("Lack of authorization");
        }
    }

    public boolean isOrganizer(Tournament tournament, User user) {
        return tournament != null && tournament.getOrganizer() != null && user != null
                && Objects.equals(tournament.getOrganizer().getId(), user.getId());
    }

    public boolean isReferee(Tournament tournament, User user) {
        if (tournament == null || user == null) {
            return false;
        }
        return tournament.getReferees().stream()
                .anyMatch(ref -> Objects.equals(ref.getId(), user.getId()));
    }

    public boolean isCoachOfTeam(Team team, User user) {
        return team != null && team.getCoach() != null && user != null
                && Objects.equals(team.getCoach().getId(), user.getId());
    }

    public void ensureTeamManager(Team team, User user) {
        if (user == null) {
            throw new AccessDeniedException("Lack of authorization");
        }
        if (isOrganizer(team.getTournament(), user)) {
            return;
        }
        if (!isCoachOfTeam(team, user)) {
            throw new AccessDeniedException("Lack of authorization");
        }
    }

    public boolean canViewMatch(Match match, User user) {
        if (match == null || match.getTournament() == null) {
            return false;
        }
        Tournament tournament = match.getTournament();
        if (canViewTournament(tournament, user)) {
            return true;
        }
        if (user == null) {
            return false;
        }
        return isCoachOfTeam(match.getTeamHome(), user) || isCoachOfTeam(match.getTeamAway(), user)
                || isMatchReferee(match, user);
    }

    public void ensureCanViewMatch(Match match, User user) {
        if (!canViewMatch(match, user)) {
            throw new AccessDeniedException("Lack of authorization");
        }
    }

    public boolean isMatchReferee(Match match, User user) {
        return match != null && match.getReferee() != null && user != null
                && Objects.equals(match.getReferee().getId(), user.getId());
    }

    public void ensureCanManageMatchEvents(Match match, User user) {
        if (match == null) {
            throw new AccessDeniedException("Lack of authorization");
        }
        Tournament tournament = match.getTournament();
        if (isOrganizer(tournament, user)) {
            return;
        }
        if (isMatchReferee(match, user)) {
            return;
        }
        throw new AccessDeniedException("Insufficient permissions to manage match events");
    }

    public void ensureCoachForMatchTeam(Match match, User user, Team team) {
        if (match == null || team == null) {
            throw new AccessDeniedException("Lack of authorization");
        }
        Tournament tournament = match.getTournament();
        if (isOrganizer(tournament, user)) {
            return;
        }
        if (isCoachOfTeam(team, user)) {
            return;
        }
        throw new AccessDeniedException("Lack of authorization");
    }
}

