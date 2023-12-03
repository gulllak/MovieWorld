package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.MinimumDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальное количество символов - 200")
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private Mpa mpa;
    @JsonIgnore
    private Set<Long> likes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Genre> genres;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Director> directors;
}
