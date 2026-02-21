package com.gabmeula.nexusdistapi.dtos.UserOAuth2Client;

public record ShowUserOAuth2ClientDTO(
        String clientId,
        String clientSecret,
        String name,
        String scopes) {
}
