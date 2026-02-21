package com.gabmeula.nexusdistapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabmeula.nexusdistapi.Services.UserService;
import com.gabmeula.nexusdistapi.dtos.User.CreateUserDTO;
import com.gabmeula.nexusdistapi.dtos.User.ShowUserDTO;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    public UserService userService;

    @PostMapping("/")
    public ResponseEntity<ShowUserDTO> createUser(@RequestBody @Valid CreateUserDTO entity) {

        ShowUserDTO user = this.userService.createUser(entity);

        return ResponseEntity.ok(user);
    }

}
