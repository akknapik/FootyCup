package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.common.MatchEventRef;
import com.tournament.app.footycup.backend.dto.common.PlayerRef;
import com.tournament.app.footycup.backend.dto.common.TeamRef;
import com.tournament.app.footycup.backend.dto.common.UserRef;
import com.tournament.app.footycup.backend.model.MatchEvent;
import com.tournament.app.footycup.backend.model.Player;
import com.tournament.app.footycup.backend.model.Team;
import com.tournament.app.footycup.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommonMapper {
    @Named("toUserRef")
    default UserRef toUserRef(User user) {
        if(user == null) return null;
        var name = (user.getFirstname() + " " + user.getLastname()).trim();
        return new UserRef(user.getId(), name, user.getEmail());
    }

    @Named("toUserRefList")
    default List<UserRef> toUserRefList(List<User> users) {
        return users == null ? List.of() : users.stream().map(this::toUserRef).toList();
    }

    @Named("toPlayerRef")
    default PlayerRef toPlayerRef(Player player) {
        if(player == null) return null;
        return new PlayerRef(player.getId(), player.getNumber(), player.getName(), player.getBirthDate());
    }

    @Named("toPlayerRefList")
    default List<PlayerRef> toPlayerRefList(List<Player> players) {
        return players == null ? List.of() : players.stream().map(this::toPlayerRef).toList();
    }

    @Named("toTeamRef")
    default TeamRef toTeamRef(Team team) {
        if(team == null) return null;
        return new TeamRef(team.getId(), team.getName());
    }

    @Named("toTeamRefList")
    default List<TeamRef> toTeamRefList(List <Team> teams) {
        return teams == null ? List.of() : teams.stream().map(this::toTeamRef).toList();
    }

    @Named("toMatchEventRef")
    default MatchEventRef toMatchEventRef(MatchEvent matchEvent) {
        if(matchEvent == null) return null;
        return new MatchEventRef(matchEvent.getId(), this.toTeamRef(matchEvent.getTeam()),
                this.toPlayerRef(matchEvent.getPlayer()), matchEvent.getEventType().name(), matchEvent.getMinute());
    }

    default List<MatchEventRef> toMatchEventRefList(List<MatchEvent> events) {
        return events == null ? List.of() : events.stream().map(this::toMatchEventRef).toList();
    }
}
