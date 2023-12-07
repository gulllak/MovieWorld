package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@RequiredArgsConstructor
public class Director {
    private final Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private final String name;
}
