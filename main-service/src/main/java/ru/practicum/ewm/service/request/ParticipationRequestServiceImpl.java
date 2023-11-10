package ru.practicum.ewm.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.ParticipationMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.StateType;
import ru.practicum.ewm.model.enums.StatusType;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserParticipationRequestByPrivate(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!(event.getInitiator().equals(user))) {
            throw new ConflictException("User with id=" + user.getId() + " doesnt initiator event with id=" + event.getId());
        }
        List<ParticipationRequest> requests = participationRepository.findAllByEvent(event);
        return requests.stream()
                .map(ParticipationMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateParticipationRequestByPrivate(Long userId, Long eventId,
                                                                              EventRequestStatusUpdateRequest request) {

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        List<ParticipationRequest> requests = participationRepository.findAllByIds(request.getRequestIds());
        if ((!event.getRequestModeration()) || (event.getParticipantLimit() == 0)) {
            requests.forEach(participationRequest -> participationRequest.setStatus(StatusType.CONFIRMED));
            result.setConfirmedRequests(requests.stream()
                    .map(ParticipationMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
            return result;
        }

        List<ParticipationRequest> confirmedRequests =
                participationRepository.findAllByEvent_IdAndStatus(event.getId(), StatusType.CONFIRMED);
        if (request.getStatus().equals(StatusType.CONFIRMED) && confirmedRequests.size() >= event.getParticipantLimit()) {
            throw new ConflictException("Limit of participants to the event with id=" + eventId);
        }
        requests.stream()
                .filter(participationRequest -> !(participationRequest.getStatus().equals(StatusType.PENDING)))
                .forEach(participationRequest -> {
                    throw new ConflictException("Participation Request must be pending");
                });

        List<ParticipationRequest> requestList = new ArrayList<>();

        for (ParticipationRequest participationRequest : requests) {
            participationRequest.setStatus(request.getStatus());
            requestList.add(participationRequest);
        }
        if (request.getStatus().equals(StatusType.CONFIRMED)) {
            result.setConfirmedRequests(requestList
                    .stream()
                    .map(ParticipationMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }

        if (request.getStatus().equals(StatusType.REJECTED)) {
            result.setRejectedRequests(requestList
                    .stream()
                    .map(ParticipationMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        List<ParticipationRequest> requests = participationRepository.findAllByRequester_Id(userId);
        if (requests.size() > 0) {
            return requests.stream()
                    .map(ParticipationMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Transactional
    @Override
    public ParticipationRequestDto saveParticipationRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (participationRepository.findByEventAndRequester(event, user) != null) {
            throw new ConflictException("User with id=" + userId +
                    " cannot add request for participation to the event with id=" + eventId);
        }
        if (event.getInitiator().equals(user)) {
            throw new ConflictException("User with id=" + user.getId() + " cannot apply to participate in the " +
                    "event with id=" + event.getId());
        }
        if (!(event.getState().equals(StateType.PUBLISHED))) {
            throw new ConflictException("Event with id=" + eventId + " is not published");
        }

        int confirmedRequests = participationRepository.findAllByEvent_IdAndStatus(event.getId(), StatusType.CONFIRMED).size();
        if (event.getParticipantLimit() != null && event.getParticipantLimit() != 0
                && event.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Cannot add request for participation to the event with id=" + eventId);
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setRequester(user);
        participationRequest.setEvent(event);
        participationRequest.setStatus(!event.getRequestModeration()
                || event.getParticipantLimit() == 0 ? StatusType.CONFIRMED : StatusType.PENDING);
        return ParticipationMapper.toParticipationRequestDto(participationRepository.save(participationRequest));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelParticipationRequestByUser(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        ParticipationRequest participationRequest = participationRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Participation request with id=" + requestId + " was not found"));
        if (!participationRequest.getRequester().equals(user)) {
            throw new NotFoundException("User with id=" + user.getId() + " is not requester request with id=" + requestId);
        }
        participationRequest.setStatus(StatusType.CANCELED);
        return ParticipationMapper.toParticipationRequestDto(participationRequest);
    }
}
