package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.tournament.TournamentItemResponse;
import com.tournament.app.footycup.backend.dto.tournament.TournamentResponse;
import com.tournament.app.footycup.backend.model.Tournament;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MappingConfig.class, uses = CommonMapper.class)
public interface TournamentMapper {
    @Mapping(target = "status", expression = "java(tournament.getStatus().name())")
    @Mapping(target = "organizer", source = "organizer", qualifiedByName = "toUserRef")
    TournamentItemResponse toItem(Tournament tournament);

    @Mapping(target = "status", expression = "java(tournament.getStatus().name())")
    @Mapping(target = "system", expression = "java(tournament.getSystem() != null ? tournament.getSystem().name() : null)")
    @Mapping(target = "organizer", source = "organizer", qualifiedByName = "toUserRef")
    @Mapping(target = "referees", expression = "java(tournament.getReferees().stream().map(commonMapper::toUserRef).toList())")
    TournamentResponse toResponse(Tournament tournament, @Context CommonMapper commonMapper);
}
