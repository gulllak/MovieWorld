package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

@Data
@Builder
@RequiredArgsConstructor
public class Event {
    private final Long eventId;
    private final Long userId;
    private final Long entityId;
    private final EventType eventType;
    private final Operation operation;
    private final Long timestamp;
}
