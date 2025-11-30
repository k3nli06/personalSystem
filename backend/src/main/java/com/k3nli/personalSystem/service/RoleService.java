package com.k3nli.personalSystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.RoleDto;
import com.k3nli.personalSystem.persistence.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    RoleRepository repository;

    public List<RoleDto> findRoles() {
        return repository.findAll().stream().map(r -> RoleDto.toDto(r)).toList();
    }

}
