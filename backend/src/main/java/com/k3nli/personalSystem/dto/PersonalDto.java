package com.k3nli.personalSystem.dto;

public record PersonalDto(Long id, String name, String email, String password, DepartmentDto department, String workstation) {

}
