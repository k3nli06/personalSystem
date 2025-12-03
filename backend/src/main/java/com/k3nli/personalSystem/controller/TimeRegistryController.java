package com.k3nli.personalSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.dto.TimeRegistryRequest;
import com.k3nli.personalSystem.service.TimeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/time-registry")
public class TimeRegistryController {

    @Autowired
    TimeService service;

    @PreAuthorize("hasAuthority('human resources') || authentication.name == #id.toString()")
    @PostMapping("/{id}")
    public ResponseEntity<?> postMethodName(@PathVariable(name = "id") Long id, @RequestBody TimeRegistryRequest time) {        
        service.addHour(id, time.time());
        return ResponseEntity.ok().build();
    }
    

}
