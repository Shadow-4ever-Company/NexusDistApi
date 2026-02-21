package com.gabmeula.nexusdistapi.auth.repository;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;

import com.gabmeula.nexusdistapi.domain.UserOAuth2Client;
import com.gabmeula.nexusdistapi.repository.UserOAuth2Client.UserOAuth2ClientRepository;

@Repository
public class DbRegisteredClientRepository implements RegisteredClientRepository {

    private final UserOAuth2ClientRepository userOAuth2ClientRepository;

    public DbRegisteredClientRepository(UserOAuth2ClientRepository userOAuth2ClientRepository) {
        this.userOAuth2ClientRepository = userOAuth2ClientRepository;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        // Clients are created via POST /users/me/clients; no-op for token endpoint flow
    }

    @Override
    public RegisteredClient findById(String id) {
        return userOAuth2ClientRepository.findById(java.util.UUID.fromString(id))
                .map(this::toRegisteredClient)
                .orElse(null);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return userOAuth2ClientRepository.findByClientId(clientId)
                .map(this::toRegisteredClient)
                .orElse(null);
    }

    private RegisteredClient toRegisteredClient(UserOAuth2Client entity) {
        Set<String> redirectUriSet = parseCsv(entity.getRedirectUris());
        Set<String> scopeSet = parseCsv(entity.getScopes());
        Set<AuthorizationGrantType> grantTypes = parseGrantTypes(entity.getGrantTypes());

        var builder = RegisteredClient.withId(entity.getId().toString())
                .clientId(entity.getClientId())
                .clientSecret(entity.getClientSecretEncoded())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);

        for (AuthorizationGrantType gt : grantTypes) {
            builder.authorizationGrantType(gt);
        }
        for (String uri : redirectUriSet) {
            builder.redirectUri(uri.trim());
        }
        for (String scope : scopeSet) {
            builder.scope(scope.trim());
        }

        return builder.build();
    }

    private static Set<String> parseCsv(String value) {
        if (value == null || value.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private static Set<AuthorizationGrantType> parseGrantTypes(String value) {
        if (value == null || value.isBlank()) {
            return Set.of(AuthorizationGrantType.CLIENT_CREDENTIALS);
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(s -> {
                    if ("CLIENT_CREDENTIALS".equals(s)) return AuthorizationGrantType.CLIENT_CREDENTIALS;
                    if ("AUTHORIZATION_CODE".equals(s)) return AuthorizationGrantType.AUTHORIZATION_CODE;
                    if ("REFRESH_TOKEN".equals(s)) return AuthorizationGrantType.REFRESH_TOKEN;
                    return null;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
