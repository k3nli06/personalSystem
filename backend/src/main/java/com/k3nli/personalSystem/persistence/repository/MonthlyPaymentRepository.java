package com.k3nli.personalSystem.persistence.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.k3nli.personalSystem.persistence.entity.MonthlyPayment;

@Repository
public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPayment, Long> {

    public Optional<MonthlyPayment> findByPersonalIdAndPaymentMonth(Long PersonalId, YearMonth paymentMonth);
    public List<MonthlyPayment> findAllByPersonalId(Long PersonalId);
    public List<MonthlyPayment> findAllByPaymentMonth(YearMonth paymentMonth);

}
