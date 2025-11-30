package com.k3nli.personalSystem.service;

import java.util.List;
import java.util.Optional;

import com.k3nli.personalSystem.dto.PersonalDto;

public interface PersonalService {

    public PersonalDto addPersonal(PersonalDto personal);
    public Optional<PersonalDto> findPersonal(Long id);
    public List<PersonalDto> findAllPersonal();
    public PersonalDto updatePersonal(Long id, PersonalDto personal);
    public void updatePersonalPassword(Long id, String Password);
    public boolean deletePersonal(Long id);

}
