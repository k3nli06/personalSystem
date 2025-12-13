package com.k3nli.personalSystem.dto;

import com.k3nli.personalSystem.persistence.entity.Status;

public record StatusDto(Status status) {

    public static Status toEntity(StatusDto statusDto) {
        if (statusDto.status.toString() == Status.APPROVED.toString()) {
            return Status.APPROVED;
        } else {
            return Status.REJECTED;
        }
    }
    
}
