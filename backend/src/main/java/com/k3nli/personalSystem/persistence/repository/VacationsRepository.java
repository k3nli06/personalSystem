package com.k3nli.personalSystem.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.k3nli.personalSystem.persistence.entity.Vacations;

@Repository
public interface VacationsRepository extends JpaRepository<Vacations, Long> {

    List<Vacations> findByPersonalIdAndStartAfter(Long id, LocalDateTime dateTime);
    List<Vacations> findByStartAfter(LocalDateTime dateTime);

}
