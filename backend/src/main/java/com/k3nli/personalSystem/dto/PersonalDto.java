package com.k3nli.personalSystem.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.k3nli.personalSystem.persistence.entity.Personal;
import com.k3nli.personalSystem.persistence.entity.Role;

public record PersonalDto(Long id, String name, String email, String password, DepartmentDto department,
        List<RoleDto> role, String workstation, BigDecimal baseSalary) {

    public static PersonalDto toDto(Personal personal) {
        List<RoleDto> rolesDto = personal.getRoles().stream().map(
                r -> RoleDto.toDto(r)).toList();

        return new PersonalDto(personal.getId(), personal.getName(), personal.getEmail(), null,
                DepartmentDto.toDto(personal.getDepartment()), rolesDto, personal.getWorkstation(),
                personal.getBaseSalary());
    }

    public static Personal toEntity(PersonalDto personalDto) {
        Set<Role> roles = personalDto.role.stream().map(
                r -> RoleDto.toEntity(r)).collect(Collectors.toSet());

        var personal = new Personal(personalDto.id(), personalDto.name, personalDto.email, personalDto.password,
                DepartmentDto.toEntity(personalDto.department), personalDto.workstation, personalDto.baseSalary);
        personal.setRoles(roles);

        return personal;
    }

}
