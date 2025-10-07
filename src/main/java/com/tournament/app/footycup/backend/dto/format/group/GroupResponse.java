package com.tournament.app.footycup.backend.dto.format.group;

import com.tournament.app.footycup.backend.dto.common.GroupTeamRef;
import java.util.List;

public record GroupResponse(
        Long id,
        String name,
        List<GroupTeamRef> groupTeams) {
}
