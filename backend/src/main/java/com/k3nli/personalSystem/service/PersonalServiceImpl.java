package com.k3nli.personalSystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.DepartmentDto;
import com.k3nli.personalSystem.dto.PersonalDto;
import com.k3nli.personalSystem.dto.RoleDto;
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
        Personal saved = new Personal(null, personal.name(), personal.email(),
                passwordEncoder.encode(personal.password()),
                DepartmentDto.toEntity(personal.department()), personal.workstation(), personal.baseSalary());

        personal.role().stream().forEach(
                r -> saved.getRoles().add(RoleDto.toEntity(r)));
        saved.setId(repository.save(saved).getId());

        return PersonalDto.toDto(saved);
    }

    @Override
    public Optional<PersonalDto> findPersonal(Long id) {
        Optional<Personal> personal = repository.findById(id);

        if (!personal.isPresent()) {
            return Optional.empty();
        }
        
        PersonalDto personalDto = PersonalDto.toDto(personal.get());
        return Optional.of(personalDto);
    }

    @Override
    public List<PersonalDto> findAllPersonal() {
        return repository.findAll().stream().map(
                p -> PersonalDto.toDto(p)
            ).toList();
    }

    @Override
    public PersonalDto updatePersonal(Long id, PersonalDto personal) {
        Personal saved = repository.findById(id).get();
        saved.setName(personal.name());
        saved.setEmail(personal.email());
        saved.setDepartment(DepartmentDto.toEntity(personal.department()));
        saved.setWorkstation(personal.workstation());
        saved.setBaseSalary(personal.baseSalary());
        personal.role().stream().forEach(
                r -> saved.getRoles().add(RoleDto.toEntity(r)));
        repository.save(saved);

        return PersonalDto.toDto(saved);
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
        if (!saved.isPresent()) {
            return false;
        }
        repository.delete(saved.get());
        return true;
    }

}
