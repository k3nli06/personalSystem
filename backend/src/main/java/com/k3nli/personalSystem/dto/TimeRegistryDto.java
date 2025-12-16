package com.k3nli.personalSystem.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimeRegistryDto(UUID id, PersonalDto personal, LocalDateTime inHour, LocalDateTime outHour) {
    
}
