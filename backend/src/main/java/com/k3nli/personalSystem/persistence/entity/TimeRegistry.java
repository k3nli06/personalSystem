package com.k3nli.personalSystem.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "time_registry")
public class TimeRegistry {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne()
    @JoinColumn(name = "personal_id", nullable = false)
    private Personal personal;
    private LocalDateTime inHour;
    private LocalDateTime outHour;
    
    public TimeRegistry() {}

    public TimeRegistry(UUID id, Personal personal, LocalDateTime inHour, LocalDateTime outHour) {
        this.id = id;
        this.personal = personal;
        this.inHour = inHour;
        this.outHour = outHour;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public LocalDateTime getInHour() {
        return inHour;
    }

    public void setInHour(LocalDateTime inHour) {
        this.inHour = inHour;
    }

    public LocalDateTime getOutHour() {
        return outHour;
    }

    public void setOutHour(LocalDateTime outHour) {
        this.outHour = outHour;
    }

}