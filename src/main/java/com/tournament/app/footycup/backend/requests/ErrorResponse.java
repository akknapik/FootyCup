package com.tournament.app.footycup.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
