package ru.practicum.ewm.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.aspect.AddStat;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventLikeDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.param.EventPublicParam;
import ru.practicum.ewm.dto.event.param.EventRatingParam;
import ru.practicum.ewm.model.enums.SortLikeType;
import ru.practicum.ewm.model.enums.SortType;
import ru.practicum.ewm.service.event.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;

    @AddStat
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Long> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                      @RequestParam(required = false)
                                      @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                      @RequestParam(required = false) SortType sort,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting events by params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}," +
                "sort={}, from={}, size={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        EventPublicParam param = new EventPublicParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        if (from != null) {
            param.setFrom(from);
        }
        if (size != null) {
            param.setSize(size);
        }
        return eventService.getAllEventsByPublic(param);
    }

    @AddStat
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable Long eventId) {
        log.info("Getting event by id: {}", eventId);
        return eventService.getEventByIdByPublic(eventId);
    }

    @GetMapping("/ratings")
    @ResponseStatus(HttpStatus.OK)
    public List<EventLikeDto> getRating(@RequestParam(required = false) Long category,
                                        @RequestParam(required = false) Boolean paid,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                        @RequestParam(required = false) SortLikeType sort,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting events rating by params: category={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}," +
                "sort={}, from={}, size={}", category, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        EventRatingParam param = new EventRatingParam(category, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        if (from != null) {
            param.setFrom(from);
        }
        if (size != null) {
            param.setSize(size);
        }
        return eventService.getEventsRating(param);
    }

    @GetMapping("/{eventId}/ratings")
    @ResponseStatus(HttpStatus.OK)
    public EventLikeDto getRatingById(@PathVariable Long eventId) {
        log.info("Getting event rating by id: {}", eventId);
        return eventService.getEventRatingById(eventId);
    }
}
