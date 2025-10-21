package com.tournament.app.footycup.backend.mapper;

import com.tournament.app.footycup.backend.dto.schedule.ScheduleEntryResponse;
import com.tournament.app.footycup.backend.dto.schedule.ScheduleListItemResponse;
import com.tournament.app.footycup.backend.dto.schedule.ScheduleResponse;
import com.tournament.app.footycup.backend.model.Schedule;
import com.tournament.app.footycup.backend.model.ScheduleEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MappingConfig.class, uses = CommonMapper.class)
public interface ScheduleMapper {
    ScheduleListItemResponse toListItem(Schedule schedule);

    @Mapping(target = "match", source = "match", qualifiedByName = "toMatchRef")
    ScheduleEntryResponse toEntry(ScheduleEntry scheduleEntry);

    @Mapping(target = "entries", expression = "java(schedule.getEntries().stream().map(this::toEntry).toList())")
    ScheduleResponse toResponse(Schedule schedule);
}
