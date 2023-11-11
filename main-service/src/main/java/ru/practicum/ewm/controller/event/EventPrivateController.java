package ru.practicum.ewm.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventRequestDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.request.ParticipationRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Getting user events by user id: {}", userId);
        return eventService.getUserEventsByPrivate(userId, PageRequest.of(from > 0 ? from / size : 0, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@PathVariable Long userId, @Valid @RequestBody EventRequestDto requestDto) {

        log.info("Saving new event: {} - STARTED", requestDto);
        EventFullDto event = eventService.saveUserEventByPrivate(userId, requestDto);
        log.info("Saving new event: {} - FINISHED", event);
        return event;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Getting event by id: {} and by user id: {}", eventId, userId);
        return eventService.getUserEventByIdByPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("Updating event with id: {} and initiator id: {}", eventId, userId);
        return eventService.updateUserEventByPrivate(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getParticipationRequest(@PathVariable Long userId,
                                                                 @PathVariable Long eventId) {
        log.info("Getting requests to participate in the event with id: {} and user id: {}", eventId, userId);
        return requestService.getUserParticipationRequestByPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchParticipationRequest(@PathVariable Long userId,
                                                                    @PathVariable Long eventId,
                                                                    @Valid
                                                                    @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Getting requests to participate in the event with id: {} and user id: {}", eventId, userId);
        return requestService.updateParticipationRequestByPrivate(userId, eventId, request);
    }
}
