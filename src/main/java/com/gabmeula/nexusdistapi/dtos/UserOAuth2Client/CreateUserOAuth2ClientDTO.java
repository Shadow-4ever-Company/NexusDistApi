package com.gabmeula.nexusdistapi.dtos.UserOAuth2Client;

import jakarta.validation.constraints.NotBlank;

public record CreateUserOAuth2ClientDTO(
        @NotBlank(message = "name is required") String name,
        String redirectUris,
        String scopes) {

    public CreateUserOAuth2ClientDTO(String name) {
        this(name, null, "read");
    }
}
