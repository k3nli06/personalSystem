package com.k3nli.personalSystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.PersonalDto;
import com.k3nli.personalSystem.dto.TimeRegistryDto;
import com.k3nli.personalSystem.persistence.entity.TimeRegistry;
import com.k3nli.personalSystem.persistence.repository.PersonalRepository;
import com.k3nli.personalSystem.persistence.repository.TimeRegistryRepository;

@Service
public class TimeServiceImpl implements TimeService {

    private TimeRegistryRepository timeRep;
    private PersonalRepository personalRep;

    @Autowired
    public TimeServiceImpl(TimeRegistryRepository timeRep, PersonalRepository personalRep) {
        this.timeRep = timeRep;
        this.personalRep = personalRep;
    }

    private TimeRegistry getTodayRegistryEntity(Long id) {
        List<TimeRegistry> todayTime = timeRep.findByPersonalIdAndInHourAfter(id, LocalDate.now().atStartOfDay());
        
        if(todayTime.isEmpty()) {
            return null;
        }
        return todayTime.getFirst();
    }

    @Override
    public TimeRegistryDto getTodayRegistry(Long id) {
        TimeRegistry time = getTodayRegistryEntity(id);

        if(time == null) {
            return null;
        }
        return new TimeRegistryDto(time.getId(), PersonalDto.toDto(time.getPersonal()), time.getInHour(), time.getOutHour()); 
    }

    @Override
    public List<TimeRegistryDto> getAllPersonalTodayRegistry() {
        return timeRep.findByInHourAfter(LocalDate.now().atStartOfDay()).stream().map(
            t -> new TimeRegistryDto(t.getId(), PersonalDto.toDto(t.getPersonal()), t.getInHour(), t.getOutHour())
        ).toList();
    }

    @Override
    public void addHour(Long id, LocalDateTime time) {
        TimeRegistry registry = getTodayRegistryEntity(id);

        if(registry == null) {
            timeRep.save(new TimeRegistry(null, personalRep.findById(id).get(), time, null));
        } else {
            registry.setOutHour(time);
            timeRep.save(registry);
        }

    }

    @Override
    public List<TimeRegistryDto> getRegistriesBetween(Long id, LocalDate fromDate, LocalDate atDate) {
        List<TimeRegistry> times = timeRep.findByPersonalIdAndInHourBetween(id, fromDate.atStartOfDay(), atDate.atTime(23, 59));
        return times.stream().map(
            t -> new TimeRegistryDto(t.getId(), PersonalDto.toDto(t.getPersonal()), t.getInHour(), t.getOutHour())
        ).toList();
    }

    @Override
    public TimeRegistryDto updateRegistry(UUID id, TimeRegistryDto timeRegistry) {
        TimeRegistry registry = timeRep.findById(id).get();
        registry.setInHour(timeRegistry.inHour());
        registry.setOutHour(timeRegistry.outHour());
        timeRep.save(registry);
        return new TimeRegistryDto(id, PersonalDto.toDto(registry.getPersonal()), registry.getInHour(), registry.getOutHour());
    }

}
