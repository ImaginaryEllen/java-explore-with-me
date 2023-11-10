package ru.practicum.ewm.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.model.enums.StateType;
import ru.practicum.ewm.dto.event.param.EventAdminParam;
import ru.practicum.ewm.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> get(@RequestParam(required = false) List<Long> users,
                                  @RequestParam(required = false) List<StateType> states,
                                  @RequestParam(required = false) List<Long> categories,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting events: by users={}, by states={}, by categories={}, after={} and before={}",
                users, states, categories, rangeStart, rangeEnd);
        EventAdminParam param = new EventAdminParam(users, states, categories, rangeStart, rangeEnd);
        if (from != null) {
            param.setFrom(from);
        }
        if (size != null) {
            param.setSize(size);
        }
        return eventService.getAllEventsByAdmin(param);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patch(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequest updateEvent) {
        log.info("Updating event by id: {}", eventId);
        return eventService.updateEventByAdmin(eventId, updateEvent);
    }
}
