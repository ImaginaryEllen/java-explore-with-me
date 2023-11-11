package ru.practicum.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;
import ru.practicum.server.service.StatsService;

import java.util.List;

@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatsService service;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsDtoResponse> get(@RequestParam String start,
                                      @RequestParam String end,
                                      @RequestParam(required = false) String[] uris,
                                      @RequestParam(required = false, defaultValue = "false") boolean unique) {
        log.info("Getting session statistics with params: " +
                "start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatsDtoResponse post(@RequestBody StatsDtoRequest request) {
        log.info("Saving information about hit with request = {}", request);
        return service.postHit(request);
    }
}
