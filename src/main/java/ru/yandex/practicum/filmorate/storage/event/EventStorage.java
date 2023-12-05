package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface EventStorage {
    List<Event> getFeedByUserId(Long id);

    void addEvent(Long userId, Long entityId, EventType eventType, Operation operation);
}
