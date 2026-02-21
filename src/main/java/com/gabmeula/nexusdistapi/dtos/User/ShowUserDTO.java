package com.gabmeula.nexusdistapi.dtos.User;

import java.util.UUID;

import com.gabmeula.nexusdistapi.domain.UserRole;

public record ShowUserDTO(
                UUID id,
                String name,
                String email,
                UserRole role) {
}
