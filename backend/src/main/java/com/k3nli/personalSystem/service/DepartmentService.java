package com.k3nli.personalSystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.DepartmentDto;
import com.k3nli.personalSystem.persistence.repository.DepartmentRepository;

@Service
public class DepartmentService {
    
    @Autowired
    DepartmentRepository repository;

    public List<DepartmentDto> findDepartments() {
        return repository.findAll().stream().map(d -> DepartmentDto.toDto(d)).toList();
    }

}
