package com.gabmeula.nexusdistapi.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabmeula.nexusdistapi.Services.UserOAuth2ClientService;
import com.gabmeula.nexusdistapi.domain.User;
import com.gabmeula.nexusdistapi.dtos.User.ShowUserDTO;
import com.gabmeula.nexusdistapi.dtos.UserOAuth2Client.CreateUserOAuth2ClientDTO;
import com.gabmeula.nexusdistapi.dtos.UserOAuth2Client.ShowUserOAuth2ClientDTO;
import com.gabmeula.nexusdistapi.repository.User.UserRepository;

import jakarta.validation.Valid;

/**
 * Endpoints do usuário logado (token JWT do login normal).
 * Não usa OAuth2 client_id: o usuário autenticou com email/senha e recebeu JWT em POST /auth/login.
 */
@RestController
@RequestMapping("/users/me")
public class UsersMeController {

    private final UserRepository userRepository;
    private final UserOAuth2ClientService userOAuth2ClientService;

    public UsersMeController(UserRepository userRepository, UserOAuth2ClientService userOAuth2ClientService) {
        this.userRepository = userRepository;
        this.userOAuth2ClientService = userOAuth2ClientService;
    }

    @GetMapping
    public ResponseEntity<ShowUserDTO> me(Authentication authentication) {
        UUID userId = resolveUserId(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return ResponseEntity.ok(new ShowUserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()));
    }

    /**
     * Cria um App Client (OAuth2) para este usuário. O App Client usa fluxo OAuth2
     * (client_credentials) em POST /oauth2/token para obter token e acessar a API (ex.: ETL).
     */
    @PostMapping("/clients")
    public ResponseEntity<ShowUserOAuth2ClientDTO> createClient(
            Authentication authentication,
            @RequestBody @Valid CreateUserOAuth2ClientDTO dto) {
        UUID userId = resolveUserId(authentication);
        ShowUserOAuth2ClientDTO created = userOAuth2ClientService.create(userId, dto);
        return ResponseEntity.status(201).body(created);
    }

    private static UUID resolveUserId(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new NotUserJwtException("Expected JWT principal");
        }
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            throw new NotUserJwtException("Token subject is not a user id (e.g. app client token)");
        }
    }

    @ExceptionHandler(NotUserJwtException.class)
    public ResponseEntity<String> handleNotUserJwt(NotUserJwtException e) {
        return ResponseEntity.status(403).body(e.getMessage());
    }

    /** Token válido mas não é de usuário (ex.: é de App Client OAuth2). */
    @SuppressWarnings("serial")
    public static final class NotUserJwtException extends RuntimeException {
        NotUserJwtException(String message) {
            super(message);
        }
    }
}
