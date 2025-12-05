package com.k3nli.personalSystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.k3nli.personalSystem.dto.TimeRegistryDto;

public interface TimeService {

    public TimeRegistryDto getTodayRegistry(Long id);
    public List<TimeRegistryDto> getAllPersonalTodayRegistry();
    public void addHour(Long id, LocalDateTime time);
    public List<TimeRegistryDto> getRegistriesBetween(Long id, LocalDate fromDate, LocalDate atDate);
    public TimeRegistryDto updateRegistry(UUID id, TimeRegistryDto timeRegistry);

}
