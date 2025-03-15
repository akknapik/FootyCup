package com.tournament.app.footycup.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/tournament")
public class TournamentController {
    private static final Map<Integer, Map<String, String>> tournaments = new
            HashMap<>() {{
                put(1, Map.of("id", "1", "name", "Dunajec Winter Cup", "startDate", "15.03.2025", "endDate", "15.03.2025", "id_organizer", "1", "teams", ""));
                put(2, Map.of("id", "2", "name", "Glinik Winter Cup", "startDate", "20.03.2025", "endDate", "21.03.2025", "id_organizer", "2", "teams", ""));
            }};
}
