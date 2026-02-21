package com.gabmeula.nexusdistapi.dtos.User;

import com.gabmeula.nexusdistapi.domain.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotBlank(message = "name is required") String name,

        @NotBlank(message = "email is required") @Email String email,

        @NotBlank(message = "password is required") @Size(min = 6) String password,

        UserRole role) {

    public CreateUserDTO(String name, String email, String password) {
        this(name, email, password, null);
    }
}
