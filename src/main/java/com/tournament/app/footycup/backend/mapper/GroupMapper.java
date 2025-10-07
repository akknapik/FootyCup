package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.format.group.GroupResponse;
import com.tournament.app.footycup.backend.model.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MappingConfig.class, uses = CommonMapper.class)
public interface GroupMapper {
    @Mapping(target = "groupTeams", source = "groupTeams", qualifiedByName = "toGroupTeamsRefList")
    GroupResponse toResponse(Group group);
}
