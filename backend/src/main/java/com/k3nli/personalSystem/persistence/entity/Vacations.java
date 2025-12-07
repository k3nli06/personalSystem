package com.k3nli.personalSystem.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Vacations {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "personal_id", nullable = false)
    private Personal personal;
    private LocalDateTime start;
    private LocalDateTime finish;
    @Enumerated(EnumType.STRING)
    private Status status;

    public Vacations(Long id, Personal personal, LocalDateTime start, LocalDateTime finish, Status status) {
        this.id = id;
        this.personal = personal;
        this.start = start;
        this.finish = finish;
        this.status = status;
    }

}
