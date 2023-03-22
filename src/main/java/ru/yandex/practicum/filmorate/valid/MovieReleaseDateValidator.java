package ru.yandex.practicum.filmorate.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MovieReleaseDateValidator implements ConstraintValidator<MovieReleaseDate, LocalDate> {
    private LocalDate date;
    DateTimeFormatter moviesBirthday = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(MovieReleaseDate constraintAnnotation) {
        this.date = LocalDate.parse(constraintAnnotation.value(), moviesBirthday);
    }

    @Override
    public boolean isValid(LocalDate target, ConstraintValidatorContext context) {
        return !target.isBefore(date);
    }
}