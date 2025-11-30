package com.k3nli.personalSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.dto.LoginDto;
import com.k3nli.personalSystem.service.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto login) {
        return ResponseEntity.ok(service.login(login));
    }
    
}
