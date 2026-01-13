package com.k3nli.personalSystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import com.k3nli.personalSystem.persistence.entity.MonthlyPayment;

public record MonthlyPaymentDto(Long id, PersonalDto personalDto, LocalDateTime generatedDate, YearMonth paymentMonth,
        BigDecimal percentageHealthInsurance, BigDecimal percentageTaxWithHoldings,
        BigDecimal totalPercentageDeductions,
        BigDecimal salaryReduction, BigDecimal Bonuses, BigDecimal overtimes, BigDecimal grossSalary,
        BigDecimal netSalary) {

    public static MonthlyPaymentDto toDto(MonthlyPayment payment) {
        PersonalDto personalDto = new PersonalDto(payment.getPersonal().getId(), payment.getPersonal().getName(), null,
                null, null, null, null, payment.getPersonal().getBaseSalary());

        return new MonthlyPaymentDto(payment.getId(), personalDto,
                payment.getGeneratedDate(),
                payment.getPaymentMonth(), payment.getPercentageHealthInsurance(),
                payment.getPercentageTaxWithholdings(),
                payment.getTotalPercentageDeductions(), payment.getSalaryReductions(), payment.getBonuses(),
                payment.getOvertime(),
                payment.getGrossSalary(), payment.getNetSalary());
    }

}
