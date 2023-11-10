package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventRequestDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.StateType;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public static EventFullDto toEventFullDto(Event event) {
        EventFullDto fullDto = new EventFullDto();
        fullDto.setId(event.getId());
        fullDto.setAnnotation(event.getAnnotation());
        fullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        fullDto.setCreatedOn(event.getCreatedOn());
        fullDto.setDescription(event.getDescription());
        fullDto.setEventDate(event.getEventDate());
        fullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        fullDto.setLocation(event.getLocation());
        fullDto.setPaid(event.getPaid() == null ? Boolean.FALSE : event.getPaid());
        fullDto.setParticipantLimit(event.getParticipantLimit() == null ? Long.valueOf(0L) : event.getParticipantLimit());
        fullDto.setRequestModeration(event.getRequestModeration() == null ? Boolean.TRUE : event.getRequestModeration());
        fullDto.setPublishedOn(event.getPublishedOn());
        fullDto.setState(event.getState());
        fullDto.setTitle(event.getTitle());
        fullDto.setViews(event.getViews() == null ? Long.valueOf(0L) : event.getViews());
        return fullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto shortDto = new EventShortDto();
        shortDto.setAnnotation(event.getAnnotation());
        shortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        shortDto.setEventDate(event.getEventDate());
        shortDto.setId(event.getId());
        shortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        shortDto.setPaid(event.getPaid());
        shortDto.setTitle(event.getTitle());
        return shortDto;
    }

    public static Event toEvent(User user, Location location, Category category, EventRequestDto requestDto) {
        Event event = new Event();
        event.setAnnotation(requestDto.getAnnotation());
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(requestDto.getDescription());
        event.setEventDate(requestDto.getEventDate());
        event.setInitiator(user);
        event.setLocation(location);
        event.setPaid(requestDto.getPaid() != null ? requestDto.getPaid() : Boolean.FALSE);
        event.setParticipantLimit(requestDto.getParticipantLimit() != null ? requestDto.getParticipantLimit() : Long.valueOf(0L));
        event.setRequestModeration(requestDto.getRequestModeration() != null ? requestDto.getRequestModeration() : Boolean.TRUE);
        event.setState(StateType.PENDING);
        event.setTitle(requestDto.getTitle());
        return event;
    }
}
