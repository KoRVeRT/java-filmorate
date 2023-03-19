package ru.yandex.practicum.filmorate.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = MovieReleaseDateValidator.class)
@Documented
public @interface MovieReleaseDate {
    String value();

    String message() default "release date — no earlier than December 28, 1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}