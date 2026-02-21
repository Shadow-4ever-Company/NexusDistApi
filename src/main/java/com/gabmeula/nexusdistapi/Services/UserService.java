package com.gabmeula.nexusdistapi.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gabmeula.nexusdistapi.domain.User;
import com.gabmeula.nexusdistapi.domain.UserRole;
import com.gabmeula.nexusdistapi.repository.User.UserRepository;
import com.gabmeula.nexusdistapi.dtos.User.CreateUserDTO;
import com.gabmeula.nexusdistapi.dtos.User.ShowUserDTO;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public ShowUserDTO createUser(CreateUserDTO newUserData) {
        String password = passwordEncoder.encode(newUserData.password());
        UserRole role = newUserData.role() != null ? newUserData.role() : UserRole.COMMON;
        User user = new User(
                newUserData.name(),
                newUserData.email(),
                password,
                role);
        this.userRepository.save(user);
        return new ShowUserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }

}