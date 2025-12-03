package com.k3nli.personalSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.dto.RoleDto;
import com.k3nli.personalSystem.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/role")
@PreAuthorize("hasAuthority('human resources')")
public class RoleController {
    
    @Autowired
    RoleService service;

    @GetMapping
    public ResponseEntity<List<RoleDto>> getRoles() {
        return ResponseEntity.ok(service.findRoles());        
    }

}
