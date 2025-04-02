package com.tournament.app.footycup.backend.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {
    public final Map<String, String > tokenStore = new HashMap<>();

    public String generateToken(String email) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, email);
        return token;
    }

    public String getEmailFromToken(String token) {
        return tokenStore.get(token);
    }

    public boolean isValid(String token) {
        return tokenStore.containsKey(token);
    }
}
