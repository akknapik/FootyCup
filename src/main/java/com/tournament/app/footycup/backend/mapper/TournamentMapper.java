package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.tournament.TournamentItemResponse;
import com.tournament.app.footycup.backend.dto.tournament.TournamentResponse;
import com.tournament.app.footycup.backend.model.Tournament;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MappingConfig.class, uses = CommonMapper.class)
public interface TournamentMapper {

    @Mapping(target = "status", expression = "java(tournament.getStatus().name())")
    @Mapping(target = "organizer", source = "tournament.organizer", qualifiedByName = "toUserRef")
    @Mapping(target = "publicVisible", source = "tournament.publicVisible")
    @Mapping(target = "followed", expression = "java(followed)")
    TournamentItemResponse toItem(Tournament tournament, boolean followed);

    default TournamentItemResponse toItem(Tournament tournament) {
        return toItem(tournament, false);
    }

    @Mapping(target = "status", expression = "java(tournament.getStatus().name())")
    @Mapping(target = "system", expression = "java(tournament.getSystem() != null ? tournament.getSystem().name() : null)")
    @Mapping(target = "organizer", source = "tournament.organizer", qualifiedByName = "toUserRef")
    @Mapping(target = "referees", source = "tournament.referees", qualifiedByName = "toUserRefList")
    @Mapping(target = "publicVisible", source = "tournament.publicVisible")
    @Mapping(target = "followed", expression = "java(followed)")
    TournamentResponse toResponse(Tournament tournament, boolean followed);

    default TournamentResponse toResponse(Tournament tournament) {
        return toResponse(tournament, false);
    }
}
