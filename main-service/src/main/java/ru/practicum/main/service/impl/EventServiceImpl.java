package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.*;
import ru.practicum.main.exception.IncorrectEventException;
import ru.practicum.main.exception.IncorrectRequestException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.mapper.RequestMapper;
import ru.practicum.main.model.User;
import ru.practicum.main.model.event.*;
import ru.practicum.main.model.request.ParticipationRequest;
import ru.practicum.main.model.request.ParticipationRequestStatus;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.RequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.EventService;
import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CategoryMapper categoryMapper;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        getUser(userId);
        Pageable page = PageRequest.of(from, size);
        List<EventShortDto> eventList = eventRepository.findByInitiatorId(userId, page).stream()
                .map(eventMapper::eventToEventShortDto).collect(Collectors.toList());

        log.debug("Получен список событий, добавленных пользователем с id={}", userId);
        return eventList;
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found."));
    }

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        User user = getUser(userId);
        Event event = eventMapper.newEventDtoToEvent(newEventDto);

        if (LocalDateTime.now().plusHours(2).isAfter(event.getEventDate())) {
            throw new IncorrectEventException("Время события не может быть раньше," +
                    " чем через два часа от текущего момента");
        }

        LocalDateTime createdTime = LocalDateTime.now().withNano(0);
        event.setCreateOn(createdTime);
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        eventRepository.save(event);

        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setLocation(new EventLocation(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon()));
        eventFullDto.setCreatedOn(createdTime.format(dateTimeFormatter));

        log.debug("Сохранён объект события {}", event);
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventByOwner(Long userId, Long eventId) {
        getUser(userId);
        getEvent(eventId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId);
        checkOwner(event, userId);

        log.debug("Получен объект события {}", event);
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByOwner(Long userId, Long eventId,
                                           UpdateEventUserRequestDto updateEventUserRequestDto) {
        getUser(userId);
        Event event = getEvent(eventId);
        checkOwner(event, userId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new IncorrectEventException("Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации");
        }

        if (LocalDateTime.now().plusHours(2).isAfter(event.getEventDate())) {
            throw new IncorrectEventException("Время события не может быть раньше," +
                    " чем через два часа от текущего момента");
        }

        if (updateEventUserRequestDto.getTitle() != null) {
            event.setTitle(updateEventUserRequestDto.getTitle());
        }

        if (updateEventUserRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequestDto.getAnnotation());
        }

        if (updateEventUserRequestDto.getCategory() != null) {
            event.setCategory(categoryMapper.categoryDtoToCategory(updateEventUserRequestDto.getCategory()));
        }

        if (updateEventUserRequestDto.getDescription() != null) {
            event.setDescription(updateEventUserRequestDto.getDescription());
        }

        if (updateEventUserRequestDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEventUserRequestDto.getEventDate(), dateTimeFormatter));
            if (event.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
                throw new IncorrectEventException("Дата начала изменяемого события должна быть не ранее " +
                        "чем за час от даты публикации");
            }
        }

        if (updateEventUserRequestDto.getLocation() != null) {
            event.setLat(updateEventUserRequestDto.getLocation().getLat());
            event.setLon(updateEventUserRequestDto.getLocation().getLon());
        }

        if (updateEventUserRequestDto.getPaid() != null) {
            event.setPaid(updateEventUserRequestDto.getPaid());
        }

        if (updateEventUserRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequestDto.getParticipantLimit());
        }

        if (updateEventUserRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequestDto.getRequestModeration());
        }

        if (updateEventUserRequestDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(EventState.PENDING);
        } else if (updateEventUserRequestDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            event.setState(EventState.CANCELED);
        }

        event = eventRepository.save(event);

        log.debug("Обновлён объект события: {}", event);
        return eventMapper.eventToEventFullDto(event);
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found."));
    }

    private void checkOwner(Event event, Long userId) {
        if (!userId.equals(event.getInitiator().getId())) {
            throw new IncorrectEventException("Пользователь с id=" + userId +
                    " не является создателем события " + event);
        }
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByOwner(Long userId, Long eventId) {
        getUser(userId);
        Event event = getEvent(eventId);
        checkOwner(event, userId);

        List<ParticipationRequestDto> requestList = requestRepository.findAllByEventId(event.getId()).stream()
                .map(requestMapper::participationRequestToParticipationRequestDto).collect(Collectors.toList());

        log.debug("Получен список запросов к событию с id={}", eventId);
        return requestList;
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestsByOwner(Long userId, Long eventId,
                                                                     EventRequestStatusUpdateRequest requests) {
        getUser(userId);
        Event event = getEvent(eventId);
        checkOwner(event, userId);

        List<ParticipationRequest> foundRequests = requestRepository.findAllById(requests.getRequestIds());

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new IncorrectRequestException("Подтверждение заявок для события с id=" +  eventId + " не требуется");
        }

        if (event.getParticipantLimit() <= requestRepository.countParticipationRequestByEventIdAndStatus(eventId,
                ParticipationRequestStatus.CONFIRMED)) {
            throw new IncorrectRequestException("Достигнут лимит по заявкам для события с id=" +  eventId);
        }

        for (ParticipationRequest request : foundRequests) {
            if (!request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                throw new IncorrectRequestException("Статус можно изменить только у заявок, " +
                        "находящихся в состоянии ожидания");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        switch (requests.getStatus()) {
            case CONFIRMED:
                for (ParticipationRequest request : foundRequests) {
                    if (event.getParticipantLimit()
                            <= requestRepository.countParticipationRequestByEventIdAndStatus(eventId,
                            ParticipationRequestStatus.CONFIRMED)) {
                        request.setStatus(ParticipationRequestStatus.REJECTED);
                        rejectedRequests.add(requestMapper.participationRequestToParticipationRequestDto(request));
                        requestRepository.save(request);
                    }
                    request.setStatus(ParticipationRequestStatus.CONFIRMED);
                    confirmedRequests.add(requestMapper.participationRequestToParticipationRequestDto(request));
                    requestRepository.save(request);
                }
                break;
            case REJECTED:
                for (ParticipationRequest request : foundRequests) {
                    request.setStatus(ParticipationRequestStatus.REJECTED);
                    rejectedRequests.add(requestMapper.participationRequestToParticipationRequestDto(request));
                    requestRepository.save(request);
                }
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);

        log.debug("Изменены статусы заявок для события с id={}", eventId);
        return result;
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                               String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        LocalDateTime start;
        LocalDateTime end;

        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }

        if (rangeStart == null) {
            end = LocalDateTime.of(2130, 10, 10, 10, 10, 10);
        } else {
            end = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }

        List<Event> events = eventRepository.findByAdmin(users, states, categories, start, end, page);
        List<EventFullDto> result = events.stream()
                .map(eventMapper::eventToEventFullDto).collect(Collectors.toList());

        log.debug("Получена подборка событий от имени администратора");
        return result;
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto updateEvent) {
        Event event = getEvent(eventId);

        if (!event.getState().equals(EventState.PENDING) && updateEvent.getStateAction()
                .equals(StateAction.PUBLISH_EVENT)) {
            throw new IncorrectEventException("Событие можно публиковать," +
                    " только если оно в состоянии ожидания публикации");
        }

        if (event.getState().equals(EventState.PUBLISHED) && updateEvent.getStateAction()
                .equals(StateAction.REJECT_EVENT)) {
            throw new IncorrectEventException("Событие можно отклонить, только если оно еще не опубликовано ");
        }

        if (updateEvent.getTitle() != null) {
            event.setTitle(updateEvent.getTitle());
        }

        if (updateEvent.getAnnotation() != null) {
            event.setAnnotation(updateEvent.getAnnotation());
        }

        if (updateEvent.getDescription() != null) {
            event.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateEvent.getEventDate(), dateTimeFormatter));
            if (event.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
                throw new IncorrectEventException("Дата начала изменяемого события должна быть не ранее " +
                        "чем за час от даты публикации");
            }
        }

        if (updateEvent.getLocation() != null) {
            event.setLon(updateEvent.getLocation().getLon());
            event.setLat(updateEvent.getLocation().getLat());
        }

        if (updateEvent.getPaid() != null) {
            event.setPaid(updateEvent.getPaid());
        }

        if (updateEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEvent.getParticipantLimit());
        }

        if (updateEvent.getRequestModeration() != null) {
            event.setRequestModeration(updateEvent.getRequestModeration());
        }

        if (event.getState().equals(EventState.PENDING) && updateEvent.getStateAction()
                .equals(StateAction.PUBLISH_EVENT)) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        if (!event.getState().equals(EventState.PUBLISHED) && updateEvent.getStateAction()
                .equals(StateAction.REJECT_EVENT)) {
            event.setState(EventState.CANCELED);
            event.setPublishedOn(null);
        }

        event = eventRepository.save(event);

        log.debug("Обновлён объект события: {}", event);
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByPublic(String text, List<Long> categoriesId, Boolean paid, String rangeStart,
                                                 String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                                 Integer size, HttpServletRequest request) {
        Pageable page = PageRequest.of(from, size);

        List<EventShortDto> events = eventRepository.findByPublic(text, categoriesId, paid, EventState.PUBLISHED.name(),
                        page).stream()
                .filter(event -> rangeStart != null ?
                        event.getEventDate().isAfter(LocalDateTime.parse(rangeStart, dateTimeFormatter)) :
                        event.getEventDate().isAfter(LocalDateTime.now())
                                && rangeEnd != null ? event.getEventDate().isBefore(LocalDateTime.parse(rangeEnd,
                                dateTimeFormatter)) : event.getEventDate().isBefore(LocalDateTime.MAX))
                .map(eventMapper::eventToEventShortDto).collect(Collectors.toList());

        if (onlyAvailable.equals(true)) {
            events = events.stream().filter(shortEventDto -> shortEventDto.getConfirmedRequests() < eventRepository
                    .findById(shortEventDto.getId()).get().getParticipantLimit() || eventRepository
                    .findById(shortEventDto.getId()).get().getParticipantLimit() == 0).collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    events = events.stream().sorted(Comparator.comparing(EventShortDto::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case "VIEWS":
                    events = events.stream().sorted(Comparator.comparing(EventShortDto::getViews))
                            .collect(Collectors.toList());
                    break;
                default:
                    throw new IncorrectEventException("Некорректный вариант сортировки в запросе");
            }
        }

        List<EventShortDto> result = events.stream().peek(shortEventDto -> getViews(shortEventDto.getId()))
                .peek(shortEventDto -> shortEventDto.setViews(getViews(shortEventDto.getId())))
                .collect(Collectors.toList());
        statsClient.createHit(request);

        log.debug("Получен список запросов с учетом фильтров");
        return result;
    }

    private Long getViews(long eventId) {
        ResponseEntity<Object> responseEntity = statsClient.getStats(LocalDateTime.MIN.toString(),
                LocalDateTime.now().toString(), List.of("/events/" + eventId),false);

        if (Objects.equals(responseEntity.getBody(), "")) {
            return ((Map<String, Long>) responseEntity.getBody()).get("hits");
        } else {
            return 0L;
        }
    }

    @Override
    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        Event event = getEvent(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IncorrectEventException("Доступны только опубликованные события");
        }

        EventFullDto eventFullDto = eventMapper.eventToEventFullDto(event);
        eventFullDto.setViews(getViews(eventId));
        statsClient.createHit(request);

        log.debug("Получен объект события {}", event);
        return eventFullDto;
    }
}
