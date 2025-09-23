package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.dto.team.CreatePlayerRequest;
import com.tournament.app.footycup.backend.dto.team.CreateTeamRequest;
import com.tournament.app.footycup.backend.dto.team.UpdatePlayerRequest;
import com.tournament.app.footycup.backend.dto.team.UpdateTeamRequest;
import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.PlayerRepository;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import com.tournament.app.footycup.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class TeamService {
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Team getTeamById(Long tournamentId, Long id, User organizer) {
        var team = teamRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Team not found") );
        if(!team.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Team for that tournament ID not found");
        }
        if(!team.getTournament().getOrganizer().getId().equals(organizer.getId()) && !team.getCoach().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }
        return team;
    }

    @Transactional(readOnly = true)
    public List<Team> getTeamsByTournamentId(Long tournamentId, User organizer) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        if(!tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }
        var teams = teamRepository.findByTournamentId(tournamentId);
        return teams;
    }

    @Transactional
    public Team createTeam(Long tournamentId, CreateTeamRequest request, User organizer) {
        var tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found"));
        var coach = userRepository.findByEmail(request.coachEmail())
                .orElseThrow(() -> new NoSuchElementException("Coach not found"));
        if(!tournament.getOrganizer().getId().equals(organizer.getId())) {
            throw new AccessDeniedException("Lack of authorization");
        }
        var team = new Team();
        team.setName(request.name());
        team.setCountry(request.country());
        team.setCoach(coach);
        team.setTournament(tournament);
        return teamRepository.save(team);
    }

    @Transactional
    public Team updateTeam(Long tournamentId, Long id, UpdateTeamRequest updated, User organizer) {
        var team = getTeamById(tournamentId, id, organizer);
        if(updated.name() != null) team.setName(updated.name());
        if(updated.coachEmail() != null) {
            var coach = userRepository.findByEmail(updated.coachEmail())
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            team.setCoach(coach);
        }
        if(updated.country() != null) team.setCountry(updated.country());
        return teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long tournamentId, Long id, User organizer) {
        var team = getTeamById(tournamentId, id, organizer);
        teamRepository.delete(team);
    }

    @Transactional
    public void deleteTeams(Long tournamentId, User organizer) {
        var teams = getTeamsByTournamentId(tournamentId, organizer);
        teamRepository.deleteAll(teams);
    }

    @Transactional
    public Team addPlayer(Long tournamentId, Long id, CreatePlayerRequest request, User organizer) {
        var team = getTeamById(tournamentId, id, organizer);
        var player = new Player();
        player.setName(request.name());
        player.setNumber(request.number());
        player.setBirthDate(request.birthDate());
        player.setTeam(team);
        team.getPlayerList().add(player);
        playerRepository.save(player);
        return teamRepository.save(team);
    }

    @Transactional
    public Team updatePlayer(Long tournamentId, Long id, Long playerId, UpdatePlayerRequest updated, User organizer) {
        var team = getTeamById(tournamentId, id, organizer);
        var player = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found"));
        if(!player.getTeam().getId().equals(team.getId())) {
            throw new IllegalArgumentException("Player is not on this team");
        }
        if(updated.name() != null) player.setName(updated.name());
        if(updated.number() > 0) player.setNumber(updated.number());
        if(updated.birthDate() != null) player.setBirthDate(updated.birthDate());
        playerRepository.save(player);
        return teamRepository.findById(team.getId())
                .orElseThrow(() -> new NoSuchElementException("Team not found"));
    }

    @Transactional
    public Team removePlayer(Long tournamentId, Long id, Long playerId, User organizer) {
        var team = getTeamById(tournamentId, id, organizer);
        var player = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found"));
        if(!player.getTeam().getId().equals(team.getId())) {
            throw new IllegalArgumentException("Player is not on this team");
        }
        team.getPlayerList().remove(player);
        playerRepository.delete(player);
        return team;
    }
}
