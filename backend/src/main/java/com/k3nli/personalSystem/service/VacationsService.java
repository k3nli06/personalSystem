package com.k3nli.personalSystem.service;

import java.util.List;

import com.k3nli.personalSystem.dto.VacationsDto;
import com.k3nli.personalSystem.persistence.entity.Status;

public interface VacationsService {

    public VacationsDto addVacations(Long personalId, VacationsDto vacations);
    public List<VacationsDto> findVacations(Long personalId);
    public void deleteVacations(Long vacationsId);
    public void setVacationStatus(Long vacationId, Status status);

}
