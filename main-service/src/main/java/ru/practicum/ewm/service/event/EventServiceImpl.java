package ru.practicum.ewm.service.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.event.param.EventAdminParam;
import ru.practicum.ewm.dto.event.param.EventPublicParam;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.model.enums.*;
import ru.practicum.ewm.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient client;

    @Override
    public List<EventFullDto> getAllEventsByAdmin(EventAdminParam param) {
        Pageable page = PageRequest.of(param.getFrom() > 0 ? param.getFrom() / param.getSize() : 0, param.getSize());

        QEvent qEvent = QEvent.event;
        List<BooleanExpression> expressions = new ArrayList<>();

        if (param.getUsers() != null) {
            expressions.add(qEvent.initiator.id.in(param.getUsers()));
        }
        if (param.getStates() != null) {
            expressions.add(qEvent.state.in(param.getStates()));
        }
        if (param.getCategories() != null) {
            expressions.add(qEvent.category.id.in(param.getCategories()));
        }
        if (param.getRangeEnd() != null && param.getRangeStart() != null) {
            expressions.add(qEvent.eventDate.after(param.getRangeStart())
                    .and(qEvent.eventDate.before(param.getRangeEnd())));
        }

        Optional<BooleanExpression> predicate = expressions.stream()
                .reduce(BooleanExpression::and);
        List<Event> events = predicate.map(booleanExpression -> eventRepository.findAll(booleanExpression, page).toList())
                .orElseGet(() -> eventRepository.findAll(page).toList());

        return events.stream()
                .map(EventMapper::toEventFullDto)
                .map(this::setFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (updateEvent.getLocation() != null) {
            Location location = locationRepository.save(updateEvent.getLocation());
            updateEvent.setLocation(location);
        }

        if (updateEvent.getStateAction() != null) {

            if (updateEvent.getStateAction().equals(ActionAdminType.PUBLISH_EVENT)) {
                if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new ConflictException("Event date must be no earlier than 1 hour from the date of publication");
                }
                if (!(event.getState().equals(StateType.PENDING))) {
                    throw new ConflictException("Event state is not pending: " + event.getState());
                }
                event.setState(StateType.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());

            } else {
                if (event.getState().equals(StateType.PUBLISHED)) {
                    throw new ConflictException("Cannot rejected event with state: " + event.getState());
                }
                event.setState(StateType.CANCELED);
            }
        }

        EventFullDto fullDto = EventMapper.toEventFullDto(updateFullEvent(updateEvent, event));
        return setFullDto(fullDto);
    }

    @Override
    public List<EventShortDto> getUserEventsByPrivate(Long userId, Pageable page) {
        List<Event> events = eventRepository.findAllByInitiator_Id(userId, page).toList();
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .map(this::setShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto saveUserEventByPrivate(Long userId, EventRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(requestDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + requestDto.getCategory() + " was not found"));
        Event event;

        if (requestDto.getLocation().getId() == null || requestDto.getLocation().getId() == 0L) {
            event = eventRepository.save(EventMapper.toEvent(user, locationRepository.save(requestDto.getLocation()),
                    category, requestDto));
        } else {
            Location location = locationRepository.findById(requestDto.getLocation().getId())
                    .orElseThrow(() -> new NotFoundException("Location with id=" + requestDto.getLocation().getId()
                            + " was not found"));
            event = eventRepository.save(EventMapper.toEvent(user, location, category, requestDto));
        }

        if (event.getEventDate().isBefore(event.getCreatedOn().plusHours(2))) {
            throw new ConflictException("Event date must be no earlier than 2 hours from the date of publication");
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto getUserEventByIdByPrivate(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new NotFoundException("User with id=" + user.getId() + " is not initiator event with id=" + event.getId());
        }
        EventFullDto fullDto = EventMapper.toEventFullDto(event);
        return setFullDto(fullDto);
    }

    @Transactional
    @Override
    public EventFullDto updateUserEventByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        event.setInitiator(user);


        if (event.getState().equals(StateType.PUBLISHED)) {
            throw new ConflictException("Cannot change event with state: " + event.getState());
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Event date must be no earlier than 2 hours from the date of publication");
        }

        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(ActionUserType.CANCEL_REVIEW)) {
                event.setState(StateType.CANCELED);
            }
            if (updateEvent.getStateAction().equals(ActionUserType.SEND_TO_REVIEW)) {
                event.setState(StateType.PENDING);
            }
        }

        EventFullDto fullDto = EventMapper.toEventFullDto(updateFullEvent(updateEvent, event));
        return setFullDto(fullDto);
    }

    @Override
    public List<EventShortDto> getAllEventsByPublic(EventPublicParam param) {
        Pageable page = param.getSort() != null ? getPageBySort(param.getSort(), param.getFrom(), param.getSize()) :
                PageRequest.of(param.getFrom() > 0 ? param.getFrom() / param.getSize() : 0, param.getSize());

        QEvent qEvent = QEvent.event;
        List<BooleanExpression> expressions = new ArrayList<>();

        if (param.getText() != null) {
            expressions.add(qEvent.annotation.toLowerCase().contains(param.getText().toLowerCase())
                    .or(qEvent.description.toLowerCase().contains(param.getText().toLowerCase())));
        }

        if (param.getCategories() != null) {
            expressions.add(qEvent.category.id.in(param.getCategories()));
        }

        if (param.getPaid() != null) {
            expressions.add(qEvent.paid.eq(param.getPaid()));
        }

        if (param.getRangeEnd() != null && param.getRangeStart() != null) {
            if (param.getRangeStart().isAfter(param.getRangeEnd())) {
                throw new IllegalArgumentException("Incorrect dates");
            }
            expressions.add(qEvent.eventDate.after(param.getRangeStart())
                    .and(qEvent.eventDate.before(param.getRangeEnd())));
        } else {
            expressions.add(qEvent.eventDate.after(LocalDateTime.now()));
        }

        Optional<BooleanExpression> predicate = expressions.stream().reduce(BooleanExpression::and);
        List<Event> events = predicate.map(booleanExpression -> eventRepository.findAll(booleanExpression, page).toList())
                .orElseGet(() -> eventRepository.findAll(page).toList());

        return events.stream()
                .filter(event -> event.getState().equals(StateType.PUBLISHED))
                .map(EventMapper::toEventShortDto)
                .map(this::setShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdByPublic(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!(event.getState().equals(StateType.PUBLISHED))) {
            throw new NotFoundException("Event state must be published, but we got: " + event.getState());
        }
        EventFullDto dto = EventMapper.toEventFullDto(event);
        return setFullDto(dto);
    }

    private EventShortDto setShortDto(EventShortDto shortDto) {
        Event event = eventRepository.findById(shortDto.getId())
                .orElseThrow(() -> new NotFoundException("Event with id=" + shortDto.getId() + " was not found"));

        ResponseEntity<Object> response = client.get(event.getCreatedOn(), LocalDateTime.now(),
                new String[]{"/events/" + shortDto.getId()}, Boolean.TRUE);
        ArrayList<LinkedHashMap<?, ?>> body = (ArrayList<LinkedHashMap<?, ?>>) response.getBody();

        if (body != null && body.size() > 0) {
            shortDto.setViews(((Integer) body.get(0).get("hits")).longValue());
        } else {
            shortDto.setViews(0L);
        }

        shortDto.setConfirmedRequests(getConfirmedSize(shortDto.getId(), shortDto.getViews()));
        return shortDto;
    }

    private EventFullDto setFullDto(EventFullDto fullDto) {
        ResponseEntity<Object> response = client.get(fullDto.getCreatedOn(), LocalDateTime.now(),
                new String[]{"/events/" + fullDto.getId()}, Boolean.TRUE);
        ArrayList<LinkedHashMap<?, ?>> body = (ArrayList<LinkedHashMap<?, ?>>) response.getBody();

        if (body != null && body.size() > 0) {
            fullDto.setViews(((Integer) body.get(0).get("hits")).longValue());
        } else {
            fullDto.setViews(0L);
        }

        fullDto.setConfirmedRequests(getConfirmedSize(fullDto.getId(), fullDto.getViews()));
        return fullDto;
    }

    private Long getConfirmedSize(Long eventId, Long views) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        Long confirmed = (long) participationRequestRepository.findAllByEvent_IdAndStatus(eventId,
                StatusType.CONFIRMED).size();
        event.setViews(views);
        eventRepository.save(event);
        return confirmed;
    }

    private Pageable getPageBySort(SortType sort, Integer from, Integer size) {
        PageRequest page;
        switch (sort) {
            case VIEWS:
                page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.Direction.ASC, "views");
                break;
            case EVENT_DATE:
                page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.Direction.ASC, "eventDate");
                break;
            default:
                page = PageRequest.of(from > 0 ? from / size : 0, size);
                break;
        }
        return page;
    }

    private Event updateFullEvent(UpdateEvent updateEvent, Event event) {
        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category updateCategory = categoryRepository.findById(updateEvent.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Category with id=" + updateEvent.getCategory() + " was not found"));
            event.setCategory(updateCategory);
        }
        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            LocalDateTime date = LocalDateTime.parse(updateEvent.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (date.isAfter(LocalDateTime.now())) {
                event.setEventDate(date);
            } else {
                throw new IllegalArgumentException("Event date must be after: " + LocalDateTime.now());
            }
        }
        if (updateEvent.getLocation() != null) {
            event.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null && updateEvent.getParticipantLimit() > 0) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }
        return event;
    }
}
