package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.valid.MovieReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Film {
    int id;

    @NotBlank
    String name;

    @Size(max = 200)
    String description;

    @MovieReleaseDate
    LocalDate releaseDate;

    @Positive
    int duration;
}