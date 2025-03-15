package com.tournament.app.footycup.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Map<Integer, Map<String, String>> users = new
            HashMap<>() {{
                put(1, Map.of("id", "1", "name", "Jan Kowalski", "email",
                        "jan@example.com"));
                put(2, Map.of("id", "2", "name", "Anna Nowak", "email",
                        "anna@example.com"));
            }};

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return ResponseEntity.ok(users.values());
    }
}
