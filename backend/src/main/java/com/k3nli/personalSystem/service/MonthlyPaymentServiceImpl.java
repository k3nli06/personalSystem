package com.k3nli.personalSystem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import com.k3nli.personalSystem.dto.MonthlyPaymentDto;
import com.k3nli.personalSystem.dto.PaymentRequest;
import com.k3nli.personalSystem.persistence.entity.MonthlyPayment;
import com.k3nli.personalSystem.persistence.entity.Personal;
import com.k3nli.personalSystem.persistence.entity.Status;
import com.k3nli.personalSystem.persistence.entity.TimeRegistry;
import com.k3nli.personalSystem.persistence.entity.Vacations;
import com.k3nli.personalSystem.persistence.repository.MonthlyPaymentRepository;
import com.k3nli.personalSystem.persistence.repository.PersonalRepository;
import com.k3nli.personalSystem.persistence.repository.TimeRegistryRepository;
import com.k3nli.personalSystem.persistence.repository.VacationsRepository;

@Service
public class MonthlyPaymentServiceImpl implements MonthlyPaymentService {

    MonthlyPaymentRepository Mrepository;
    PersonalRepository Prepository;
    TimeRegistryRepository Trepository;
    VacationsRepository Vrepository;

    @Autowired
    public MonthlyPaymentServiceImpl(MonthlyPaymentRepository Mrepository, PersonalRepository Prepository,
            TimeRegistryRepository Trepository, VacationsRepository Vrepository) {
        this.Mrepository = Mrepository;
        this.Prepository = Prepository;
        this.Trepository = Trepository;
        this.Vrepository = Vrepository;
    }

    @Override
    @PostAuthorize("hasAuthority('human resources') || (!returnObject.isEmpty() && returnObject.getFirst.personalDto.id.toString() == authentication.name)")
    public List<MonthlyPaymentDto> findAllPaymentsByPersonal(Long personalId) throws NotFoundException {
        if (!Prepository.existsById(personalId))
            throw new NotFoundException();
        
        return Mrepository.findAllByPersonalId(personalId).stream()
                .map(p -> MonthlyPaymentDto.toDto(p))
                .toList();
    }
    
    @Override
    @PostAuthorize("hasAuthority('human resources') || (returnObject.isPresent() && returnObject.get.personalDto.id.toString() == authentication.name)")
    public Optional<MonthlyPaymentDto> findPayment(Long id) {
        var payment = Mrepository.findById(id);
        if (payment.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(MonthlyPaymentDto.toDto(payment.get()));
    }

    @Override
    public List<MonthlyPaymentDto> findAllPaymentsByMonth(YearMonth month) {
        return Mrepository.findAllByPaymentMonth(month).stream()
                .map(p -> MonthlyPaymentDto.toDto(p))
                .toList();
    }

    @Override
    public MonthlyPaymentDto updatePayment(Long id, PaymentRequest paymentRequest) {
        MonthlyPayment payment = Mrepository.findById(paymentRequest.personalId()).get();
        YearMonth yearMonth = payment.getPaymentMonth();
        LocalDate month = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
        
        List<TimeRegistry> registries = Trepository
                .findByPersonalIdAndInHourBetween(
                    payment.getPersonal().getId(), month.atStartOfDay(), month.plusMonths(1).atStartOfDay());
        List<Vacations> vacations = Vrepository
                .findByPersonalIdAndStartLessThanEqualAndFinishGreaterThanEqualAndStatus(
                        payment.getPersonal().getId(), month.plusMonths(1).atStartOfDay(), month.atStartOfDay(), Status.APPROVED);
        BigDecimal baseSalary = payment.getPersonal().getBaseSalary();
        BigDecimal salaryReduction = calculateSalaryReduction(baseSalary, month, vacations, registries);
        BigDecimal overtimes = calculateOvertimes(baseSalary, registries);

        payment.setBaseSalary(baseSalary);
        payment.setSalaryReductions(salaryReduction);
        payment.setOvertime(overtimes);
        payment.setBonuses(BigDecimal.valueOf(paymentRequest.bonuses()));
        payment.calculateNetSalary();
        Mrepository.save(payment);

        return MonthlyPaymentDto.toDto(payment);
    }

    @Override
    public MonthlyPaymentDto generatePayment(Long personalId, BigDecimal bonuses) throws NotFoundException, Exception {
        Personal personal = Prepository.findById(personalId).orElseThrow(() -> new NotFoundException());
        LocalDate lastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        boolean isTherePayment = Mrepository
                .findByPersonalIdAndPaymentMonth(personalId, YearMonth.of(lastMonth.getYear(), lastMonth.getMonth()))
                .isPresent();
        if (isTherePayment) {
            throw new Exception();
        }

        List<TimeRegistry> registries = Trepository
                .findByPersonalIdAndInHourBetween(personalId, lastMonth.atStartOfDay(), lastMonth.plusMonths(1).atStartOfDay());
        List<Vacations> vacations = Vrepository
                .findByPersonalIdAndStartLessThanEqualAndFinishGreaterThanEqualAndStatus(
                        personalId, lastMonth.plusMonths(1).atStartOfDay(), lastMonth.atStartOfDay(), Status.APPROVED);
        BigDecimal baseSalary = personal.getBaseSalary();
        BigDecimal percentageHealthInsurance = BigDecimal.valueOf(7).setScale(2);
        BigDecimal percentageTaxWithholdings = BigDecimal.ZERO;
        BigDecimal salaryReduction = calculateSalaryReduction(baseSalary, lastMonth, vacations, registries);
        BigDecimal overtimes = calculateOvertimes(baseSalary, registries);

        if (baseSalary.compareTo(BigDecimal.valueOf(8333.34)) == -1)
            percentageTaxWithholdings = BigDecimal.ZERO.setScale(2);
        else if (baseSalary.compareTo(BigDecimal.valueOf(8333.33)) == 1 && baseSalary.compareTo(BigDecimal.valueOf(16666.67)) == -1)
            percentageTaxWithholdings = BigDecimal.valueOf(15).setScale(2);
        else if (baseSalary.compareTo(BigDecimal.valueOf(16666.66)) == 1 && baseSalary.compareTo(BigDecimal.valueOf(29666.667)) == -1)
            percentageTaxWithholdings = BigDecimal.valueOf(20.).setScale(2);
        else if (baseSalary.compareTo(BigDecimal.valueOf(29666.66)) == 1 && baseSalary.compareTo(BigDecimal.valueOf(41666.667)) == -1)
            percentageTaxWithholdings = BigDecimal.valueOf(30.).setScale(2);
        else if (baseSalary.compareTo(BigDecimal.valueOf(41666.66)) == 1)
            percentageTaxWithholdings = BigDecimal.valueOf(30.).setScale(2);

        MonthlyPayment payment = Mrepository.save(new MonthlyPayment(
                null, YearMonth.of(lastMonth.getYear(), lastMonth.getMonth()), personal, percentageHealthInsurance,
                percentageTaxWithholdings, salaryReduction, bonuses, overtimes, baseSalary));

        return MonthlyPaymentDto.toDto(payment);
    }

    private BigDecimal calculateSalaryReduction(BigDecimal baseSalary, LocalDate lastMonth, List<Vacations> vacations,
            List<TimeRegistry> registries) {
        Map<Integer, TimeRegistry> mapRegistries = registries.stream()
                .collect(Collectors.toMap(r -> r.getInHour().getDayOfMonth(), r -> r));
        int targetWorkMinutes = 60 * 8;
        int margin = 15;
        BigDecimal dailySalary = baseSalary.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);
        BigDecimal minuteSalary = dailySalary.divide(BigDecimal.valueOf(targetWorkMinutes), 4, RoundingMode.HALF_UP);
        BigDecimal reduction = BigDecimal.ZERO;

        for (int i = 1; i <= lastMonth.lengthOfMonth(); i++) {
            LocalDate today = lastMonth.withDayOfMonth(i);
            if (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY)
                continue;

            TimeRegistry registry = mapRegistries.get(i);
            boolean isOnVacations = vacations.stream().anyMatch(
                    v -> !today.isBefore(v.getStart().toLocalDate()) && !today.isAfter(v.getFinish().toLocalDate()));

            if (registry == null && isOnVacations)
                continue;
            else if (registry == null) {
                reduction = reduction.add(dailySalary);
                continue;
            } else {
                long minutesWorked = ChronoUnit.MINUTES.between(registry.getInHour(), registry.getOutHour());
                if (minutesWorked < (targetWorkMinutes - margin)) {
                    long missingMinutes = targetWorkMinutes - minutesWorked;
                    reduction = reduction.add(isOnVacations ?
                        BigDecimal.ZERO : minuteSalary.multiply(BigDecimal.valueOf(missingMinutes)));
                }
            }
        }
        return reduction.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateOvertimes(BigDecimal baseSalary, List<TimeRegistry> registries) {
        int targetWorkMinutes = 60 * 8;
        int margin = 15;
        BigDecimal overtimeSalary = baseSalary
                .divide(BigDecimal.valueOf(30.00), 4, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(targetWorkMinutes), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(2));

        return registries.stream()
                .map(t -> {
                    long minutesWorked = ChronoUnit.MINUTES.between(t.getInHour(), t.getOutHour());

                    if (t.getInHour().getDayOfWeek() == DayOfWeek.SATURDAY || t.getInHour().getDayOfWeek() == DayOfWeek.SUNDAY) {
                        return overtimeSalary.multiply(BigDecimal.valueOf(minutesWorked));
                    } else if (minutesWorked > (targetWorkMinutes + margin)) {
                        return overtimeSalary.multiply(BigDecimal.valueOf(minutesWorked - targetWorkMinutes));
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

}
