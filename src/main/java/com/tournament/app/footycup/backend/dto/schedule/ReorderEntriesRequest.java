package com.tournament.app.footycup.backend.dto.schedule;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReorderEntriesRequest(
        @NotNull List<@NotNull Long> orderedEntryIds
) {
}
