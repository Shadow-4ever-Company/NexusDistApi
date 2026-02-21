package com.gabmeula.nexusdistapi.dtos.Auth;

public record LoginResponse(
        String access_token,
        String token_type,
        long expires_in) {
}
