package com.k3nli.personalSystem.persistence.entity;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.generator.EventType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Personal implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @ManyToOne()
    @JoinColumn(name = "department_id")
    private Department department;
    private String workstation;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "personal_role",
        joinColumns = @JoinColumn(name = "personal_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    @OneToMany(mappedBy = "personal", cascade = CascadeType.PERSIST)
    private Set<TimeRegistry> times = new HashSet<>();
    @OneToMany(mappedBy = "personal", cascade = CascadeType.PERSIST)
    private Set<Vacations> vacations = new HashSet<>();
    @Column(name = "hiring_date")
    @CurrentTimestamp(event = EventType.INSERT)
    private LocalDateTime hiringDate;
    
    public Personal() {}

    public Personal(Long id, String name, String email, String password, Department department, String workstation) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.department = department;
        this.workstation = workstation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getWorkstation() {
        return workstation;
    }

    public void setWorkstation(String workstation) {
        this.workstation = workstation;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<TimeRegistry> getTimes() {
        return times;
    }

    public void setTimes(Set<TimeRegistry> times) {
        this.times = times;
    }

    public Set<Vacations> getVacations() {
        return vacations;
    }

    public void setVacations(Set<Vacations> vacations) {
        this.vacations = vacations;
    }

    public LocalDateTime getHiringDate() {
        return hiringDate;
    }

    public void setHiringDate(LocalDateTime hiringDate) {
        this.hiringDate = hiringDate;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream().map(
            rol -> new SimpleGrantedAuthority(rol.getName())
        ).toList();
    }

    @Override
    public String getUsername() {
        return id.toString();
    }
}
