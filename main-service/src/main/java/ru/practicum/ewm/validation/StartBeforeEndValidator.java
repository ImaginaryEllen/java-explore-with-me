package ru.practicum.ewm.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, Timespan> {
    @Override
    public boolean isValid(Timespan value, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = value.getRangeStart();
        LocalDateTime end = value.getRangeEnd();
        LocalDateTime now = LocalDateTime.now();
        return (start.isBefore(end)) && (start.isAfter(now)) && (end.isAfter(now));
    }
}
