package com.tournament.app.footycup.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FirstEndpoint {

    @GetMapping("/home")
    public  String getInfo() {
        return "Tu wkrótce będzie aplikacja FootyCup!";
    }
}
