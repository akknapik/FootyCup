package com.tournament.app.footycup.backend.service;

import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.Tournament;
import com.tournament.app.footycup.backend.model.User;
import com.tournament.app.footycup.backend.repository.PlayerRepository;
import com.tournament.app.footycup.backend.repository.TeamRepository;
import com.tournament.app.footycup.backend.repository.TournamentRepository;
import com.tournament.app.footycup.backend.repository.UserRepository;
import com.tournament.app.footycup.backend.requests.TeamRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TeamService {
    private final PlayerRepository playerRepository;
    private TeamRepository teamRepository;
    private TournamentRepository tournamentRepository;
    private UserRepository userRepository;

    @Autowired
    public TeamService(PlayerRepository playerRepository, TeamRepository teamRepository, TournamentRepository tournamentRepository, UserRepository userRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
    }

    public Team getTeamById(Long tournamentId, Long id, User user) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono zespolu") );

        if(!team.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("Nie znaleziono zespołu dla tego turnieju");
        }

        if(!team.getTournament().getOrganizer().getId().equals(user.getId()) && !team.getCoach().getId().equals(user.getId())) {
            throw new AccessDeniedException("Brak dostępu do zespołu");
        }
        return team;
    }

    public List<Team> getTeamsByTournamentId(Long tournamentId, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono turnieju"));

        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Brak dostępu do zespołu");
        }

        List<Team> teams = teamRepository.findByTournamentId(tournamentId);
        return teams;
    }

    public Team createTeam(Long tournamentId, TeamRequest request, User user) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono turnieju"));
        User coach = userRepository.findByEmail(request.getCoachEmail())
                .orElseThrow(() -> new NoSuchElementException("Coach not found"));

        if(!tournament.getOrganizer().getId().equals(user.getId())) {
            throw new AccessDeniedException("Brak dostępu do zespołu");
        }
        Team team = new Team();
        team.setName(request.getName());
        team.setCountry(request.getCountry());
        team.setCoach(coach);
        team.setTournament(tournament);

        return teamRepository.save(team);
    }

    public Team updateTeam(Long tournamentId, Long id, TeamRequest updatedData, User user) {
        Team team = getTeamById(tournamentId, id, user);

        if(updatedData.getName() != null) {
            team.setName(updatedData.getName());
        }

        if(updatedData.getCoachEmail() != null) {
            User coach = userRepository.findByEmail(updatedData.getCoachEmail())
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            team.setCoach(coach);
        }

        if(updatedData.getCountry() != null) {
            team.setCountry(updatedData.getCountry());
        }

        return teamRepository.save(team);
    }

    public void deleteTeam(Long tournamentId, Long id, User user) {
        Team team = getTeamById(tournamentId, id, user);
        teamRepository.delete(team);
    }

    public Team addPlayer(Long tournamentId, Long id, Player request, User user) {
        Team team = getTeamById(tournamentId, id, user);

        Player player = new Player();
        player.setName(request.getName());
        player.setNumber(request.getNumber());
        player.setBirthDate(request.getBirthDate());
        player.setTeam(team);

        team.getPlayerList().add(player);
        playerRepository.save(player);
        return teamRepository.save(team);
    }

    public Team updatePlayer(Long tournamentId, Long id, Long playerId, Player updatedData, User user) {
        Team team = getTeamById(tournamentId, id, user);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found"));

        if(!player.getTeam().getId().equals(team.getId())) {
            throw new IllegalArgumentException("Player is not on this team");
        }

        if(updatedData.getName() != null) {
            player.setName(updatedData.getName());
        }
        if(updatedData.getNumber() != null) {
            player.setNumber(updatedData.getNumber());
        }
        if(updatedData.getBirthDate() != null) {
            player.setBirthDate(updatedData.getBirthDate());
        }
        playerRepository.save(player);

        return teamRepository.findById(team.getId())
                .orElseThrow(() -> new NoSuchElementException("Team not found"));
    }

    public Team removePlayer(Long tournamentId, Long id, Long playerId, User user) {
        Team team = getTeamById(tournamentId, id, user);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player not found"));
        if(!player.getTeam().getId().equals(team.getId())) {
            throw new IllegalArgumentException("Player is not on this team");
        }
        team.getPlayerList().remove(player);
        playerRepository.delete(player);
        return team;
    }
}
