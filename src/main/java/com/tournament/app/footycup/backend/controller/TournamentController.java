package com.tournament.app.footycup.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTournamentById(@PathVariable int id) {
        if(!tournaments.containsKey(id)) {
            return  ResponseEntity.status(404).body(Map.of("error","Tournament not found"));
        }
        return ResponseEntity.ok(tournaments.get(id));
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addTournament(@RequestParam int id, @RequestParam String name, @RequestParam String startDate) {
        if(!tournaments.containsValue(id) || (!tournaments.containsValue(name) && !tournaments.containsValue(startDate))) {
            return ResponseEntity.status(400).body(Map.of("error", "Tournament already exists"));
        }

        Map<String, String> tournamentData = new HashMap<>();
        tournamentData.put("id", String.valueOf(id));
        tournamentData.put("name", name);
        tournamentData.put("startDate", startDate);

        tournaments.put(id, tournamentData);

        return new ResponseEntity<>("Tournament added successfully", HttpStatus.CREATED);
    }
}
