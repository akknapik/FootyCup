package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.match.MatchItemResponse;
import com.tournament.app.footycup.backend.dto.match.MatchResponse;
import com.tournament.app.footycup.backend.model.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MappingConfig.class, uses = CommonMapper.class)
public interface MatchMapper {
    @Mapping(target = "status", expression = "java(match.getStatus().name())")
    @Mapping(target = "teamHome", source = "teamHome", qualifiedByName = "toTeamRef")
    @Mapping(target = "teamAway", source = "teamAway", qualifiedByName = "toTeamRef")
    @Mapping(target = "referee", source = "referee", qualifiedByName = "toUserRef")
    MatchItemResponse toItem(Match match);

    @Mapping(target = "status", expression = "java(match.getStatus().name())")
    @Mapping(target = "teamHome", source = "teamHome", qualifiedByName = "toTeamRef")
    @Mapping(target = "teamAway", source = "teamAway", qualifiedByName = "toTeamRef")
    @Mapping(target = "groupId", expression = "java(match.getGroup().getId())")
    @Mapping(target = "referee", source = "referee", qualifiedByName = "toUserRef")
    MatchResponse toResponse(Match match);
}
