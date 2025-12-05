package com.k3nli.personalSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.dto.PersonalDto;
import com.k3nli.personalSystem.service.PersonalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/personal")
@PreAuthorize("hasAuthority('human resources')")
public class PersonalController {
    
    @Autowired
    PersonalService service;
    
    @GetMapping
    public ResponseEntity<?> getAllPersonal() {
        return ResponseEntity.ok().body(service.findAllPersonal());
    }

    @PreAuthorize("authentication.name == #id.toString || hasAuthority('human resources')")
    @GetMapping("/{id}")
    public ResponseEntity<PersonalDto> getPersonal(@PathVariable(name = "id") Long id) {
        var personal = service.findPersonal(id);
        
        if(personal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(personal.get());
    }

    @PostMapping
    public ResponseEntity<PersonalDto> savePersonal(@RequestBody PersonalDto personal) {        
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addPersonal(personal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalDto> updatePersonal(@PathVariable(name = "id") Long id, @RequestBody PersonalDto personal) {
        return ResponseEntity.ok().body(service.updatePersonal(id, personal));
    }

    @PreAuthorize("authentication.name == #id.toString || hasAuthority('human resources')")
    @PatchMapping("/update-password/{id}")
    public ResponseEntity<?> updatePersonalPassword(@PathVariable(name = "id") Long id, @RequestBody String Password) {
        service.updatePersonalPassword(id, Password);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonal(@PathVariable(name = "id") Long id) {
        if(!service.deletePersonal(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }

}
