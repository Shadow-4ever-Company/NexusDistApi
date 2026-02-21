package com.gabmeula.nexusdistapi.Services;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gabmeula.nexusdistapi.domain.User;
import com.gabmeula.nexusdistapi.domain.UserOAuth2Client;
import com.gabmeula.nexusdistapi.dtos.UserOAuth2Client.CreateUserOAuth2ClientDTO;
import com.gabmeula.nexusdistapi.dtos.UserOAuth2Client.ShowUserOAuth2ClientDTO;
import com.gabmeula.nexusdistapi.repository.User.UserRepository;
import com.gabmeula.nexusdistapi.repository.UserOAuth2Client.UserOAuth2ClientRepository;

@Service
public class UserOAuth2ClientService {

    private static final String CLIENT_SECRET_PREFIX = "{bcrypt}";

    private final UserOAuth2ClientRepository userOAuth2ClientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserOAuth2ClientService(
            UserOAuth2ClientRepository userOAuth2ClientRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userOAuth2ClientRepository = userOAuth2ClientRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ShowUserOAuth2ClientDTO create(UUID userId, CreateUserOAuth2ClientDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String clientId = UUID.randomUUID().toString().replace("-", "");
        String plainSecret = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        String encodedSecret = CLIENT_SECRET_PREFIX + passwordEncoder.encode(plainSecret);

        UserOAuth2Client entity = new UserOAuth2Client();
        entity.setUser(user);
        entity.setClientId(clientId);
        entity.setClientSecretEncoded(encodedSecret);
        entity.setName(dto.name());
        entity.setRedirectUris(dto.redirectUris() != null ? dto.redirectUris() : "");
        entity.setScopes(dto.scopes() != null && !dto.scopes().isBlank() ? dto.scopes() : "read");
        entity.setGrantTypes("client_credentials");

        userOAuth2ClientRepository.save(entity);

        return new ShowUserOAuth2ClientDTO(clientId, plainSecret, entity.getName(), entity.getScopes());
    }
}
