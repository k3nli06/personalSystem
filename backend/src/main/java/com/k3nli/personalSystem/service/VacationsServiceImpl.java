package com.k3nli.personalSystem.service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.DepartmentDto;
import com.k3nli.personalSystem.dto.PersonalDto;
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
        Vacations saved = vacationsRep.save(new Vacations(null, personalRep.findById(personalId).get(),
                vacations.start(), vacations.finish(), Status.PENDING));

        PersonalDto personalDto = new PersonalDto(personalId, saved.getPersonal().getName(), null, null,
                DepartmentDto.toDto(saved.getPersonal().getDepartment()),
                null, saved.getPersonal().getWorkstation(), null);
        return new VacationsDto(saved.getId(), personalDto, saved.getStart(), saved.getFinish(),
                saved.getStatus());
    }

    @Override
    @PostAuthorize("hasAuthority('human resources') || returnObject.personal.id.toString() == authentication.name")
    public VacationsDto findVacations(Long id) throws NotFoundException {
        var vacations = vacationsRep.findById(id);
        if (vacations.isEmpty()) {
            throw new NotFoundException();
        }

        PersonalDto personalDto = new PersonalDto(vacations.get().getPersonal().getId(),
                vacations.get().getPersonal().getName(), null, null,
                DepartmentDto.toDto(vacations.get().getPersonal().getDepartment()), null,
                vacations.get().getPersonal().getWorkstation(), null);
        return new VacationsDto(id, personalDto, vacations.get().getStart(),
                vacations.get().getFinish(), vacations.get().getStatus());
    }

    @Override
    public List<VacationsDto> findAllPersonalVacations() {
        LocalDate date = LocalDate.now();
        LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), 1);

        return vacationsRep.findByStartAfter(localDate.atStartOfDay()).stream().map(
                v -> {
                    PersonalDto personalDto = new PersonalDto(v.getPersonal().getId(), v.getPersonal().getName(), null,
                            null, DepartmentDto.toDto(v.getPersonal().getDepartment()), null,
                            v.getPersonal().getWorkstation(), null);
                    return new VacationsDto(v.getId(), personalDto, v.getStart(), v.getFinish(),
                            v.getStatus());
                }).toList();
    }

    @Override
    public List<VacationsDto> findVacationsByPersonal(Long personalId) {
        LocalDate date = LocalDate.now();
        LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), 1);

        return vacationsRep.findByPersonalIdAndStartAfter(personalId, localDate.atStartOfDay()).stream().map(
                v -> {
                    PersonalDto personalDto = new PersonalDto(personalId, v.getPersonal().getName(), null, null,
                            DepartmentDto.toDto(v.getPersonal().getDepartment()),
                            null, v.getPersonal().getWorkstation(), null);
                    return new VacationsDto(v.getId(), personalDto, v.getStart(), v.getFinish(),
                            v.getStatus());
                }).toList();
    }

    @Override
    public VacationsDto updateVacations(Long vacationsId, VacationsDto vacationsDto) {
        Vacations saved = vacationsRep.findById(vacationsId).get();
        saved.setStart(vacationsDto.start());
        saved.setFinish(vacationsDto.finish());
        saved.setStatus(Status.PENDING);
        vacationsRep.save(saved);

        PersonalDto personalDto = new PersonalDto(saved.getPersonal().getId(), saved.getPersonal().getName(), null,
                null, DepartmentDto.toDto(saved.getPersonal().getDepartment()), null,
                saved.getPersonal().getWorkstation(), null);
        return new VacationsDto(vacationsId, personalDto, saved.getStart(), saved.getFinish(),
                saved.getStatus());
    }

    @Override
    public boolean deleteVacations(Long vacationsId) {
        var vacation = vacationsRep.findById(vacationsId);
        if (vacation.isEmpty()) {
            return false;
        }
        vacationsRep.delete(vacation.get());
        return true;
    }

    @Override
    public void setVacationStatus(Long vacationId, Status status) {
        Vacations saved = vacationsRep.findById(vacationId).get();
        saved.setStatus(status);
        vacationsRep.save(saved);
    }

}
