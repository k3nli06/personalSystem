package com.k3nli.personalSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.service.DepartmentService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/department")
@PreAuthorize("hasAuthority('human resources')")
public class DepartmentController {
    
    @Autowired
    DepartmentService service;

    @GetMapping
    public ResponseEntity<?> getDepartments() {
        return ResponseEntity.ok(service.findDepartments());
    }    

}
