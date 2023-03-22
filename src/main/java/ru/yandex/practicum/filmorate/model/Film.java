package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.valid.MovieReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @MovieReleaseDate("1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Mpa mpa;

    private List<Genre> genres = new ArrayList<>();
}