package ru.practicum.ewm.service.request;

import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getUserParticipationRequestByPrivate(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateParticipationRequestByPrivate(Long userId, Long eventId,
                                                                       EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getParticipationRequestByUser(Long userId);

    ParticipationRequestDto saveParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelParticipationRequestByUser(Long userId, Long requestId);
}
