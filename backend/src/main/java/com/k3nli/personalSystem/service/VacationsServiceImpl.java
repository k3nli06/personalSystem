package com.k3nli.personalSystem.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.VacationsDto;
import com.k3nli.personalSystem.persistence.entity.Status;
import com.k3nli.personalSystem.persistence.entity.Vacations;
import com.k3nli.personalSystem.persistence.repository.PersonalRepository;
import com.k3nli.personalSystem.persistence.repository.VacationsRepository;

@Service
public class VacationsServiceImpl implements VacationsService {

    private VacationsRepository vacationsRep;
    private PersonalRepository personalRep;

    @Autowired
    public VacationsServiceImpl(VacationsRepository vacationsRep, PersonalRepository personalRep) {
        this.vacationsRep = vacationsRep;
        this.personalRep = personalRep;
    }

    @Override
    public VacationsDto addVacations(Long personalId, VacationsDto vacations) {
        Vacations saved = vacationsRep.save(new Vacations(null, personalRep.findById(personalId).get(), vacations.start(), vacations.finish(), Status.PENDING));
        return new VacationsDto(saved.getId(), saved.getStart(), saved.getFinish(), saved.getStatus());
    }

    @Override
    public List<VacationsDto> findVacations(Long personalId) {
        LocalDate date = LocalDate.now();
        LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), 1); 

        return vacationsRep.findByPersonalIdAndStartAfter(personalId, localDate).stream().map(
            v -> new VacationsDto(v.getId(), v.getStart(), v.getFinish(), v.getStatus())
        ).toList();
    }

    @Override
    public void deleteVacations(Long vacationsId) {
        vacationsRep.deleteById(vacationsId);
    }

    @Override
    public void setVacationStatus(Long vacationId, Status status) {
        Vacations saved = vacationsRep.findById(vacationId).get();
        saved.setStatus(status);
        vacationsRep.save(saved);
    }

}
