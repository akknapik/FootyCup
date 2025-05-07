package com.tournament.app.footycup.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeamRequest {
    private String name;
    private String country;
    private String coachEmail;
}
