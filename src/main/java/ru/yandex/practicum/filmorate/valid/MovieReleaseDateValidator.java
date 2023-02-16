package ru.yandex.practicum.filmorate.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class MovieReleaseDateValidator implements ConstraintValidator<MovieReleaseDate, LocalDate> {
    private static final LocalDate MOVIES_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public boolean isValid(LocalDate data, ConstraintValidatorContext context) {
        if (data != null) {
            return data.isAfter(MOVIES_BIRTHDAY);
        }
        return true;
    }
}