package com.k3nli.personalSystem.service;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.k3nli.personalSystem.dto.VacationsDto;
import com.k3nli.personalSystem.persistence.entity.Status;

public interface VacationsService {

    public VacationsDto addVacations(Long personalId, VacationsDto vacations);
    public VacationsDto findVacations(Long id) throws NotFoundException; 
    public List<VacationsDto> findVacationsByPersonal(Long personalId);
    public List<VacationsDto> findAllPersonalVacations();
    public VacationsDto updateVacations(Long vacationsId, VacationsDto vacationsDto);
    public boolean deleteVacations(Long vacationsId);
    public void setVacationStatus(Long vacationId, Status status);

}
