package ru.practicum.service;

import ru.practicum.StatsDtoRequest;
import ru.practicum.StatsDtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatsDtoResponse postHit(StatsDtoRequest request);

    List<StatsDtoResponse> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
