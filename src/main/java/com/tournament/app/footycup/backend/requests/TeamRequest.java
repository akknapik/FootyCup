package com.tournament.app.footycup.backend.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequest {
    private String name;
    private String country;
    private String coachEmail;
}
