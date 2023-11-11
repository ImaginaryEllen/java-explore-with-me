package ru.practicum.server.service;

import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;

import java.util.List;

public interface StatsService {
    StatsDtoResponse postHit(StatsDtoRequest request);

    List<StatsDtoResponse> getStats(String start, String end, String[] uris, boolean unique);
}
