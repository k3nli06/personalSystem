package com.k3nli.personalSystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.persistence.repository.PersonalRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    PersonalRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var saved = repository.findById(Long.valueOf(username));

        if(!saved.isPresent()) {
            throw new UsernameNotFoundException("Personal not found");
        }

        return saved.get();
    }

}
