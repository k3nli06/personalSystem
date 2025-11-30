package com.k3nli.personalSystem.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.k3nli.personalSystem.persistence.entity.TimeRegistry;

@Repository
public interface TimeRegistryRepository extends JpaRepository<TimeRegistry, UUID> {

    public List<TimeRegistry> findByPersonalIdAndInHourAfter(Long id, LocalDateTime date);
    public List<TimeRegistry> findByPersonalIdAndInHourBetween(Long id, LocalDateTime startMonth, LocalDateTime endMonth);

}