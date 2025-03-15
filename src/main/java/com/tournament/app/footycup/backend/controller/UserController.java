package com.tournament.app.footycup.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        System.out.println(id);
        if(!users.containsKey(id)) {
            return  ResponseEntity.status(404).body(Map.of("error","User not found"));
        }
        return ResponseEntity.ok(users.get(id));
    }
}
