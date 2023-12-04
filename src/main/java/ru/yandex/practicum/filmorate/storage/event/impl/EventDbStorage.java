package ru.yandex.practicum.filmorate.storage.event.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.util.EventType;
import ru.yandex.practicum.filmorate.util.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeedByUserId(Long id) {
        String sqlQuery = "SELECT * FROM events WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::createEvent, id);
    }

    @Override
    public void addEvent(Long userId, Long entityId, EventType eventType, Operation operation) {
        String sqlQuery = "INSERT INTO events (user_id, entity_id, event_type, operation, event_time) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setLong(1, userId);
            stmt.setLong(2, entityId);
            stmt.setString(3, eventType.toString());
            stmt.setString(4, operation.toString());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            return stmt;
        });
    }

    private Event createEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .timestamp(rs.getTimestamp("event_time").getTime())
                .build();
    }
}
