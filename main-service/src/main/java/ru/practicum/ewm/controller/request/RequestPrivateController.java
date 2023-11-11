package ru.practicum.ewm.controller.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.request.ParticipationRequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestPrivateController {
    private final ParticipationRequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllByUser(@PathVariable Long userId) {
        log.info("Getting requests to participate in the events by user id: {}", userId);
        return requestService.getParticipationRequestByUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto post(@Positive @PathVariable Long userId,
                                        @Positive @RequestParam Long eventId) {
        log.info("Saving new request to participate in the event with id:" +
                " {}, from user with id: {} - STARTED", eventId, userId);
        ParticipationRequestDto participationRequest = requestService.saveParticipationRequest(userId, eventId);
        log.info("Saving new request to participate in the event: {} - FINISHED", participationRequest);
        return participationRequest;
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto patch(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Updating participation request by id: {} - CANCEL - by user id: {}", requestId, userId);
        return requestService.cancelParticipationRequestByUser(userId, requestId);
    }
}
