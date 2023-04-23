package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.User;
import ru.practicum.main.model.event.Event;
import ru.practicum.main.model.request.ParticipationRequest;
import ru.practicum.main.model.request.ParticipationRequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long userId);

    Optional<ParticipationRequest> getRequestByRequesterAndEvent(User requester, Event event);

    Integer countParticipationRequestByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    List<ParticipationRequest> findAllByEventId(Long eventId);
}
