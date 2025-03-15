package com.tournament.app.footycup.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    private static final Map<Integer, Map<String, String>> tournaments = new
            HashMap<>() {{
                put(1, Map.of("id", "1", "name", "Dunajec Winter Cup", "startDate", "15.03.2025"));
                put(2, Map.of("id", "2", "name", "Glinik Winter Cup", "startDate", "20.03.2025"));
            }};

    @GetMapping
    public ResponseEntity<Object> getTournaments() {
        return ResponseEntity.ok(tournaments.values());
    }
}
