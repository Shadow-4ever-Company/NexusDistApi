package com.gabmeula.nexusdistapi.repository.UserOAuth2Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabmeula.nexusdistapi.domain.UserOAuth2Client;

public interface UserOAuth2ClientRepository extends JpaRepository<UserOAuth2Client, UUID> {

    Optional<UserOAuth2Client> findByClientId(String clientId);

    List<UserOAuth2Client> findByUser_Id(UUID userId);
}
