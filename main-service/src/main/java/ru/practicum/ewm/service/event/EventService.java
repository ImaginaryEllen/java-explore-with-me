package ru.practicum.ewm.service.event;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.event.param.EventAdminParam;
import ru.practicum.ewm.dto.event.param.EventPublicParam;
import ru.practicum.ewm.dto.event.param.EventRatingParam;

import java.util.List;

public interface EventService {
    List<EventFullDto> getAllEventsByAdmin(EventAdminParam param);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent);

    List<EventShortDto> getUserEventsByPrivate(Long userId, Pageable page);

    EventFullDto saveUserEventByPrivate(Long userId, EventRequestDto requestDto);

    EventFullDto getUserEventByIdByPrivate(Long userId, Long eventId);

    EventFullDto updateUserEventByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

    List<EventShortDto> getAllEventsByPublic(EventPublicParam param);

    EventFullDto getEventByIdByPublic(Long eventId);

    EventLikeDto getEventRatingById(Long eventId);

    List<EventLikeDto> getEventsRating(EventRatingParam param);
}
