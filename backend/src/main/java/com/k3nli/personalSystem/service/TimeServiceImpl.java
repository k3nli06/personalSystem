package com.k3nli.personalSystem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    @Override
    public void addHour(Long id, LocalDateTime time) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayAt0 = today.atStartOfDay();
        List<TimeRegistry> todayTime = timeRep.findByPersonalIdAndInHourAfter(id, todayAt0);

        if(todayTime.isEmpty()) {
            timeRep.save(new TimeRegistry(null, personalRep.findById(id).get(), time, null));
        } else {
            todayTime.getFirst().setOutHour(time);
            timeRep.save(todayTime.getFirst());
        }

    }

}
