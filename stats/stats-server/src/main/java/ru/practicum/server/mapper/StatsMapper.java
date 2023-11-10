package ru.practicum.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;
import ru.practicum.server.model.App;
import ru.practicum.server.model.Hit;

@UtilityClass
public class StatsMapper {
    public static Hit toHit(App app, StatsDtoRequest request) {
        Hit hit = new Hit();
        hit.setApp(app);
        hit.setUri(request.getUri());
        hit.setIp(request.getIp());
        hit.setTimestamp(request.getTimestamp());
        return hit;
    }

    public static StatsDtoResponse toStatsDtoResponse(Hit hit, Long hits) {
        StatsDtoResponse response = new StatsDtoResponse();
        response.setApp(hit.getApp().getName());
        response.setUri(hit.getUri());
        response.setHits(hits);
        return response;
    }

    public static App toApp(String name) {
        App app = new App();
        app.setName(name);
        return app;
    }
}
