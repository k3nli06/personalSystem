package com.k3nli.personalSystem.persistence.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.generator.EventType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Entity(name = "monthly_payment")
public class MonthlyPayment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "personal_id", nullable = false)
    private Personal personal;
    @CurrentTimestamp(event = EventType.INSERT)
    @Column(name = "generated_date")
    private LocalDateTime generatedDate;
    private YearMonth paymentMonth;
    @Column(name = "percentage_health_insurance", precision = 5, scale = 2)
    private BigDecimal percentageHealthInsurance;
    @Column(name = "percentage_tax_withholdings", precision = 5, scale = 2)
    private BigDecimal percentageTaxWithholdings;
    @Column(name = "total_percentage_deductions", precision = 5, scale = 2)
    private BigDecimal totalPercentageDeductions;
    @Column(name = "salary_reductions", precision = 19, scale = 2)
    private BigDecimal salaryReductions;
    @Column(precision = 19, scale = 2)
    private BigDecimal bonuses;
    @Column(precision = 19, scale = 2)
    private BigDecimal overtime;
    @Column(name = "base_salary", precision = 19, scale = 2)
    private BigDecimal baseSalary;
    @Column(name = "gross_salary", precision = 19, scale = 2)
    private BigDecimal grossSalary;
    @Column(name = "net_salary", precision = 19, scale = 2)
    private BigDecimal netSalary;

    public MonthlyPayment(Long id, YearMonth paymentMonth, Personal personal, BigDecimal percentageHealthInsurance,
            BigDecimal percentageTaxWithholdings, BigDecimal salaryReductions,
            BigDecimal bonuses, BigDecimal overtime, BigDecimal baseSalary) {
        this.id = id;
        this.personal = personal;
        this.paymentMonth = paymentMonth;
        this.percentageHealthInsurance = percentageHealthInsurance;
        this.percentageTaxWithholdings = percentageTaxWithholdings;
        this.totalPercentageDeductions = this.percentageHealthInsurance.add(this.percentageTaxWithholdings);
        this.salaryReductions = salaryReductions;
        this.bonuses = bonuses;
        this.overtime = overtime;
        this.baseSalary = baseSalary;
        calculateNetSalary();
    }

    public void calculateNetSalary() {
        grossSalary = baseSalary.add(bonuses).add(overtime);
        
        BigDecimal percentageDivisor = BigDecimal.valueOf(100);
        BigDecimal totalHealthInsuranceDecimal = this.percentageHealthInsurance.divide(percentageDivisor, 4, RoundingMode.HALF_UP);
        BigDecimal totalTaxWithholdingsDecimal = this.percentageTaxWithholdings.divide(percentageDivisor, 4, RoundingMode.HALF_UP);
        

        var temporalGrossSalary = grossSalary
                .subtract(grossSalary.multiply(totalHealthInsuranceDecimal));
        netSalary = temporalGrossSalary.subtract(temporalGrossSalary.multiply(totalTaxWithholdingsDecimal))
                .subtract(salaryReductions)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
}
