package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.team.TeamItemResponse;
import com.tournament.app.footycup.backend.dto.team.TeamResponse;
import com.tournament.app.footycup.backend.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MappingConfig.class, uses = CommonMapper.class)
public interface TeamMapper {
    @Mapping(target = "coach", source = "coach", qualifiedByName = "toUserRef")
    @Mapping(target = "playersCount", expression = "java(team.getPlayerList() == null ? 0 : team.getPlayerList().size())")
    TeamItemResponse toItem(Team team);

    @Mapping(target = "coach", source = "coach", qualifiedByName = "toUserRef")
    @Mapping(target = "players", source = "playerList", qualifiedByName = "toPlayerRefList")
    TeamResponse toResponse(Team team);
}
