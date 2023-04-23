package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.exception.AlreadyExistsException;
import ru.practicum.main.exception.IncorrectRequestException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.RequestMapper;
import ru.practicum.main.model.User;
import ru.practicum.main.model.event.Event;
import ru.practicum.main.model.event.EventState;
import ru.practicum.main.model.request.ParticipationRequest;
import ru.practicum.main.model.request.ParticipationRequestStatus;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.RequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.RequestService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        getUser(userId);
        List<ParticipationRequest> eventRequests = requestRepository.findAllByRequesterId(userId);

        log.debug("Получен список запросов пользователя с id={}", userId);
        return eventRequests.stream().map(requestMapper::participationRequestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);

        ParticipationRequestStatus status;
        if (!event.getRequestModeration() && event.getParticipantLimit() == 0) {
            status = ParticipationRequestStatus.CONFIRMED;
        } else {
            status = ParticipationRequestStatus.PENDING;
        }

        LocalDateTime createdTime = LocalDateTime.now().withNano(0);

        ParticipationRequest request = new ParticipationRequest(null, createdTime, event, user, status);

        if (requestRepository.getRequestByRequesterAndEvent(user, event).isPresent()) {
            throw new AlreadyExistsException("Запрос от пользователя с id " +  userId + " уже существует");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new IncorrectRequestException("Создатель события не может отправлять запрос на участие в нем");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IncorrectRequestException("Запрос не создан, так как событие ещё не опубликовано");
        }

        Integer countRequests = requestRepository.countParticipationRequestByEventIdAndStatus(eventId,
                ParticipationRequestStatus.CONFIRMED);

        if (countRequests >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new IncorrectRequestException("Запрос не создан из-за ограничения на количество участников");
        }

        if (!event.getRequestModeration()) {
            status = ParticipationRequestStatus.CONFIRMED;
        }
        request.setStatus(status);

        requestRepository.save(request);
        ParticipationRequestDto result = requestMapper.participationRequestToParticipationRequestDto(request);
        result.setCreated(createdTime.toString());

        log.debug("Сохранён объект запроса: {}", request);
        return result;
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found."));

    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found."));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        getUser(userId);
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found."));

        request.setStatus(ParticipationRequestStatus.CANCELED);
        requestRepository.save(request);

        log.debug("Отменён запрос: {}", request);
        return requestMapper.participationRequestToParticipationRequestDto(request);
    }
}
