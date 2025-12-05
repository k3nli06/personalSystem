package com.k3nli.personalSystem.dto;

import java.util.List;

public record PersonalDto(Long id, String name, String email, String password, DepartmentDto department, List<RoleDto> role, String workstation) {

}
