package com.k3nli.personalSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.dto.TimeRegistryDto;
import com.k3nli.personalSystem.dto.TimeRegistryRequest;
import com.k3nli.personalSystem.service.TimeService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/time-registry")
public class TimeRegistryController {

    @Autowired
    TimeService service;

    @PreAuthorize("hasAuthority('human resources')")
    @GetMapping
    public ResponseEntity<List<TimeRegistryDto>> getAllPersonalTodayRegistries() {
        return ResponseEntity.ok().body(service.getAllPersonalTodayRegistry());
    }
    
    @PreAuthorize("authentication.name == #id.toString()")
    @GetMapping("/{id}")
    public ResponseEntity<TimeRegistryDto> getTodayRegistry(@PathVariable(name = "id") Long id) {
        TimeRegistryDto time = service.getTodayRegistry(id);

        if(time == null) {
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(time);
    }

    @PreAuthorize("hasAuthority('human resources') || authentication.name == #id.toString()")
    @GetMapping(path = "/{id}", params = {"fromDate", "atDate"})
    public ResponseEntity<List<TimeRegistryDto>> getRegistriesByIdBetween(@PathVariable(name = "id") Long id,
            @RequestParam("fromDate") LocalDate fromDate,
            @RequestParam("atDate") LocalDate atDate) {
        return ResponseEntity.ok().body(service.getRegistriesBetween(id, fromDate, atDate));
    }

    @PreAuthorize("hasAuthority('human resources') || authentication.name == #id.toString()")
    @PostMapping("/{id}")
    public ResponseEntity<?> addhour(@PathVariable(name = "id") Long id, @RequestBody TimeRegistryRequest time) {
        service.addHour(id, time.time());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('human resources')")
    @PutMapping("/update/{id}")
    public ResponseEntity<TimeRegistryDto> putMethodName(@PathVariable(name = "id") UUID id, @RequestBody TimeRegistryDto timeRegistry) {
        return ResponseEntity.ok().body(service.updateRegistry(id, timeRegistry));
    }

}
