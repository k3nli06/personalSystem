package com.k3nli.personalSystem.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.k3nli.personalSystem.dto.MonthlyPaymentDto;
import com.k3nli.personalSystem.dto.PaymentRequest;
import com.k3nli.personalSystem.service.MonthlyPaymentService;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/monthly-payments")
@PreAuthorize("hasAuthority('human resources')")
public class MonthlyPaymentController {

    @Autowired
    MonthlyPaymentService service;

    @PreAuthorize("hasAuthority('employee') || hasAuthority('human resources')")
    @GetMapping("{id}")
    public ResponseEntity<MonthlyPaymentDto> getPaymentById(@PathVariable(name = "id") Long id) {
        var payment = service.findPayment(id);
        if (payment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(payment.get());
    }

    @GetMapping(params = "month")
    public ResponseEntity<List<MonthlyPaymentDto>> getPaymentsByMonth(@RequestParam(name = "month") YearMonth month) {
        return ResponseEntity.ok().body(service.findAllPaymentsByMonth(month));
    }

    @PreAuthorize("hasAuthority('employee') || hasAuthority('human resources')")
    @GetMapping(params = "personalId")
    public ResponseEntity<?> getPaymentsByPersonal(@RequestParam(name = "personalId") Long personalId) {
        try{
            return ResponseEntity.ok().body(service.findAllPaymentsByPersonal(personalId));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Personal not found");
        }
    }

    @PatchMapping("{id}")
    public ResponseEntity<MonthlyPaymentDto> updatePayment(@PathVariable(name = "id") Long id, @RequestBody PaymentRequest request) {
        return ResponseEntity.ok().body(service.updatePayment(id, request));
    }

    @PostMapping
    public ResponseEntity<?> generatePayment(@RequestBody PaymentRequest request) {
        try {
            return ResponseEntity.created(null)
                    .body(service.generatePayment(request.personalId(), BigDecimal.valueOf(request.bonuses())));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Personal not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Existing payment");
        }
    }

}
