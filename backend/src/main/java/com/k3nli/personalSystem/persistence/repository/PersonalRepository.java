package com.k3nli.personalSystem.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.k3nli.personalSystem.persistence.entity.Personal;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {

}