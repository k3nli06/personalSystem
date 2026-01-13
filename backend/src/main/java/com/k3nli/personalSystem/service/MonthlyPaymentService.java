package com.k3nli.personalSystem.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.k3nli.personalSystem.dto.MonthlyPaymentDto;
import com.k3nli.personalSystem.dto.PaymentRequest;

public interface MonthlyPaymentService {

    public Optional<MonthlyPaymentDto> findPayment(Long id); 
    public List<MonthlyPaymentDto> findAllPaymentsByMonth(YearMonth month);
    public List<MonthlyPaymentDto> findAllPaymentsByPersonal(Long personalId) throws NotFoundException;
    public MonthlyPaymentDto updatePayment(Long id, PaymentRequest paymentRequest);
    public MonthlyPaymentDto generatePayment(Long personalId, BigDecimal bonuses) throws NotFoundException, Exception;

}
