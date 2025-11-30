package com.k3nli.personalSystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.DepartmentDto;
import com.k3nli.personalSystem.dto.PersonalDto;
import com.k3nli.personalSystem.persistence.entity.Personal;
import com.k3nli.personalSystem.persistence.repository.PersonalRepository;

@Service
public class PersonalServiceImpl implements PersonalService {

    private PersonalRepository repository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public PersonalServiceImpl(PersonalRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PersonalDto addPersonal(PersonalDto personal) {
        Personal saved = repository.save(new Personal(null, personal.name(), personal.email(), passwordEncoder.encode(personal.password()),
                DepartmentDto.toEntity(personal.department()), personal.workstation()));

        return new PersonalDto(saved.getId(), saved.getName(), saved.getEmail(), saved.getPassword(),
                DepartmentDto.toDto(saved.getDepartment()), saved.getWorkstation());
    }

    @Override
    public Optional<PersonalDto> findPersonal(Long id) {
        Optional<Personal> personal = repository.findById(id);

        if (!personal.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new PersonalDto(id, personal.get().getName(), personal.get().getEmail(), null,
                DepartmentDto.toDto(personal.get().getDepartment()), personal.get().getWorkstation()));
    }

    @Override
    public List<PersonalDto> findAllPersonal() {
        return repository.findAll().stream().map(
                p -> new PersonalDto(p.getId(), p.getName(), p.getEmail(), null, DepartmentDto.toDto(p.getDepartment()),
                        p.getWorkstation()))
                .toList();
    }

    @Override
    public PersonalDto updatePersonal(Long id, PersonalDto personal) {
        Personal saved = repository.findById(id).get();
        saved.setName(personal.name());
        saved.setEmail(personal.email());
        saved.setDepartment(DepartmentDto.toEntity(personal.department()));
        saved.setWorkstation(personal.workstation());
        repository.save(saved);
        return new PersonalDto(saved.getId(), saved.getName(), saved.getEmail(), null, 
                DepartmentDto.toDto(saved.getDepartment()), saved.getWorkstation());
    }

    @Override
    public void updatePersonalPassword(Long id, String password) {
        var personal = repository.findById(id).get();
        personal.setPassword(passwordEncoder.encode(password));
        repository.save(personal);
    }

    @Override
    public boolean deletePersonal(Long id) {
        var saved = repository.findById(id);
        if(!saved.isPresent()) {
            return false;
        }
        repository.delete(saved.get());
        return true;
    }

}
