package com.k3nli.personalSystem.dto;

import java.time.LocalDateTime;

import com.k3nli.personalSystem.persistence.entity.Status;

public record VacationsDto(Long id, LocalDateTime start, LocalDateTime finish, Status status) {

}
