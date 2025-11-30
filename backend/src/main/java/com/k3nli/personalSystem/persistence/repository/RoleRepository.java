package com.k3nli.personalSystem.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.k3nli.personalSystem.persistence.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
}
