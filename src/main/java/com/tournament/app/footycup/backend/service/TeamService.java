package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.repository.PlayerRepository;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TeamService {
    private final PlayerRepository playerRepository;
    private TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    public Optional<Team> getTeamById(Long id) {
        Optional<Team> team = teamRepository.findById(id);
        return team;
    }

    public List<Team> getAllTeamByTournamentId(Long tournamentId) {
        List<Team> teams = teamRepository.findByTournamentId(tournamentId);
        return teams;
    }

    public Team updateTeam(Team team) {
        Team existingTeam = teamRepository.findById(team.getId())
                .orElseThrow(() -> new NoSuchElementException());

        if(team.getName() != null) existingTeam.setName(team.getName());
        if(team.getCoach() != null) existingTeam.setCoach(team.getCoach());

        return teamRepository.save(existingTeam);
    }

    public void deleteTeam(Long teamId) {
        if(!teamRepository.existsById(teamId)) {
            throw new NoSuchElementException();
        }

        teamRepository.deleteById(teamId);
    }

    public Team addPlayer(Long teamId, Player player) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException());

        player.setTeam(team);
        team.getPlayerList().add(player);
        playerRepository.save(player);

        return teamRepository.save(team);

    }

    public Team removePlayer(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException());
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException());

        if(!team.getPlayerList().contains(player)) {
            throw new IllegalArgumentException();
        }

        team.getPlayerList().remove(player);
        playerRepository.delete(player);
        return teamRepository.save(team);
    }

    public Team updatePlayer(Long teamId, Player player) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException());
        Player existingPlayer = playerRepository.findById(player.getId())
                .orElseThrow(() -> new NoSuchElementException());

        if(!team.getPlayerList().contains(existingPlayer)) {
            throw new IllegalArgumentException();
        }

        if(player.getNumber() != null) existingPlayer.setNumber(player.getNumber());
        if(player.getName() != null) existingPlayer.setNumber(player.getNumber());
        if(player.getBirthDate() != null) existingPlayer.setBirthDate(player.getBirthDate());

        playerRepository.save(existingPlayer);
        team.getPlayerList().removeIf(p -> p.getId().equals(existingPlayer.getId()));
        team.getPlayerList().add(existingPlayer);
        return teamRepository.save(team);
    }

}
