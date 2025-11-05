package com.tournament.app.footycup.backend.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;



public record ResetPasswordRequest(
        String token,
        String password) {

}