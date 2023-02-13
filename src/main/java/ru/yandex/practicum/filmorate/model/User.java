package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
public class User {
    private long id;

    @NotBlank
    private String login;

    @Email
    private String email;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<Long> friends = new HashSet<>();

    public void addFriend(long id) {
        friends.add(id);
    }

    public void removeFriend(long id) {
        friends.remove(id);
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public List<Long> getFiends() {
        return new ArrayList<>(friends);
    }
}