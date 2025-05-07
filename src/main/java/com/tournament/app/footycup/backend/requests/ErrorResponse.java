package com.tournament.app.footycup.backend.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Schema(description = "Standard error response returned by the API")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error message", example = "Resource not found")
    private String message;

    @Schema(description = "Timestamp of the error", example = "2025-05-07T14:20:00")
    private LocalDateTime timestamp;
}
