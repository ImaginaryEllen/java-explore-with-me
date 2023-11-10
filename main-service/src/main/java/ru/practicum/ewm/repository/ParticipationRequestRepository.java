package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.StatusType;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequester_Id(Long requesterId);

    ParticipationRequest findByEventAndRequester(Event event, User requester);

    @Query("select p from ParticipationRequest as p where p.id in ?1")
    List<ParticipationRequest> findAllByIds(List<Long> requestIds);

    List<ParticipationRequest> findAllByEvent_IdAndStatus(Long eventId, StatusType statusType);

    List<ParticipationRequest> findAllByEvent(Event event);
}
