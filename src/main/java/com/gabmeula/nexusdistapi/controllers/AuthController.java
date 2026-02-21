package com.gabmeula.nexusdistapi.controllers;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabmeula.nexusdistapi.domain.User;
import com.gabmeula.nexusdistapi.dtos.Auth.LoginRequest;
import com.gabmeula.nexusdistapi.dtos.Auth.LoginResponse;
import com.gabmeula.nexusdistapi.repository.User.UserRepository;

import jakarta.validation.Valid;

/**
 * Login normal: usuário envia email/senha e recebe um JWT.
 * Não usa client_id/client_secret; o App Client (ETL) usa OAuth2 em POST
 * /oauth2/token.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

        private static final long TOKEN_EXPIRY_SECONDS = 3600L;

        private final AuthenticationManager authenticationManager;
        private final JwtEncoder jwtEncoder;
        private final UserRepository userRepository;
        private final String issuer;

        public AuthController(
                        AuthenticationManager authenticationManager,
                        JwtEncoder jwtEncoder,
                        UserRepository userRepository,
                        @Value("${spring.security.oauth2.authorizationserver.issuer}") String issuer) {
                this.authenticationManager = authenticationManager;
                this.jwtEncoder = jwtEncoder;
                this.userRepository = userRepository;
                this.issuer = issuer;
        }

        @PostMapping("/login/")
        public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
                User user = userRepository.findByEmail(authentication.getName())
                                .orElseThrow();
                Instant now = Instant.now();
                JwtClaimsSet claims = JwtClaimsSet.builder()
                                .issuer(issuer)
                                .subject(user.getId().toString())
                                .audience(List.of(issuer))
                                .issuedAt(now)
                                .expiresAt(now.plusSeconds(TOKEN_EXPIRY_SECONDS))
                                .claim("scope", "read")
                                .claim("role", user.getRole().name())
                                .build();
                String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
                return ResponseEntity.ok(new LoginResponse(token, "Bearer", TOKEN_EXPIRY_SECONDS));
        }
}
