package com.gabmeula.nexusdistapi.dtos.User;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String firstName,
        String email,
        String password) {
}
