package com.k3nli.personalSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.dto.StatusDto;
import com.k3nli.personalSystem.dto.VacationsDto;
import com.k3nli.personalSystem.service.VacationsService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/vacations")
@PreAuthorize("hasAuthority('human resources')")
public class VacationsController {
    
    @Autowired
    VacationsService service;

    @GetMapping()
    public ResponseEntity<List<VacationsDto>> getAllPersonalVaations() {
        return ResponseEntity.ok().body(service.findAllPersonalVacations());
    }

    @PreAuthorize("hasAnyAuthority('human resources', 'employee')")
    @GetMapping("/{id}")
    public ResponseEntity<VacationsDto> getVacations(@PathVariable(name = "id") Long id) {
        try {
            VacationsDto vacations = service.findVacations(id);
            return ResponseEntity.ok().body(vacations);
        } catch(NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAuthority('human resources') || #id.toString == authentication.name")
    @GetMapping(params = "personal")
    public ResponseEntity<List<VacationsDto>> getVacationsByPersonalId(@RequestParam("personal") Long id) {
        return ResponseEntity.ok().body(service.findVacationsByPersonal(id));
    }
    
    @PreAuthorize("hasAuthority('human resources') || #id.toString == authentication.name")
    @PostMapping("/{id}")
    public ResponseEntity<VacationsDto> postVacations(@PathVariable(name = "id") Long id, @RequestBody VacationsDto vacations) {        
        return ResponseEntity.created(null).body(service.addVacations(id, vacations));
    }
    
    @PreAuthorize("hasAuthority('human resources') || #id.toString == authentication.name")
    @PutMapping("/{id}")
    public ResponseEntity<VacationsDto> updateVacations(@PathVariable(name = "id") Long id, @RequestBody VacationsDto vacationsDto) {
        return ResponseEntity.ok().body(service.updateVacations(id, vacationsDto));        
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> setVacationsStatus(@PathVariable(name = "id") Long id, @RequestBody StatusDto status) {
        service.setVacationStatus(id, StatusDto.toEntity(status));
        return ResponseEntity.ok().build();
    }
    
    @PreAuthorize("hasAuthority('human resources') || #id.toString == authentication.name")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVacations(@PathVariable(name = "id") Long id) {
        if(service.deleteVacations(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
