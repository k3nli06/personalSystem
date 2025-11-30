package com.k3nli.personalSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.LoginDto;
import com.k3nli.personalSystem.dto.TokenDto;
import com.k3nli.personalSystem.persistence.repository.PersonalRepository;

@Service
public class AuthenticationService {

    private AuthenticationManager authManager;
    private PersonalRepository repository;
    private JwtService jwtService;
    
    @Autowired
    public AuthenticationService(AuthenticationManager authManager, PersonalRepository repository, JwtService jwtService) {
        this.authManager = authManager;
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public TokenDto login(LoginDto login) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(login.id(), login.password()));
        String token = jwtService.getToken(repository.findById(login.id()).get());
        return new TokenDto(token);
    }

}
