package com.k3nli.personalSystem.dto;

import com.k3nli.personalSystem.persistence.entity.Role;

public record RoleDto(Long id, String name) {
    
    public static Role toEntity(RoleDto dto) {
        return new Role(dto.id, dto.name);
    }

    public static RoleDto toDto(Role entity) {
        return new RoleDto(entity.getId(), entity.getName());
    }

}
