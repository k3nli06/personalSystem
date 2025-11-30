package com.k3nli.personalSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/role")
public class RoleController {
    
    @Autowired
    RoleService service;

    @GetMapping
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(service.findRoles());        
    }

}
