package com.k3nli.personalSystem.dto;

import com.k3nli.personalSystem.persistence.entity.Department;

public record DepartmentDto(Long id, String name) {
    
    public static Department toEntity(DepartmentDto departmentDto) {
        return new Department(departmentDto.id, departmentDto.name);
    }

    public static DepartmentDto toDto(Department department) {
        return new DepartmentDto(department.getId(), department.getName());
    }

}
