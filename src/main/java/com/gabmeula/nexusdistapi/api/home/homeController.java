package com.gabmeula.nexusdistapi.api.home;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/")
public class homeController {

    @GetMapping("")
    public ResponseEntity<HashMap<String, String>> root() {

        HashMap<String, String> responseMessage = new HashMap<>();
        responseMessage.put("msg", "Seja bem vindo ao Nexus Distribution API");
        return ResponseEntity.ok().body(responseMessage);

    }

}
