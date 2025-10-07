package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.format.bracket.BracketNodeResponse;
import com.tournament.app.footycup.backend.model.BracketNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MappingConfig.class, uses = CommonMapper.class)
public interface BracketMapper {
    @Mapping(target = "parentHomeNodeId", source = "parentHomeNode.id")
    @Mapping(target = "parentAwayNodeId", source = "parentAwayNode.id")
    @Mapping(target = "match", source = "match", qualifiedByName = "toMatchRef")
    BracketNodeResponse toResponse(BracketNode node);
}
