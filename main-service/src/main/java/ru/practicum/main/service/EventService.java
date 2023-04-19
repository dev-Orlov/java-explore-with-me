package ru.practicum.main.service;

import ru.practicum.main.dto.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    @Transactional
    EventFullDto create(Long userId, NewEventDto eventNewDto);

    EventFullDto getEventByOwner(Long userId, Long eventId);

    EventFullDto updateEventByOwner(Long userId, Long eventId,
                                    UpdateEventUserRequestDto UpdateEventUserRequestDto);

    List<ParticipationRequestDto> getEventRequestsByOwner(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestsByOwner(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                        String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto updateEvent);

    List<EventShortDto> getEventsByPublic(String text, List<Long> categoriesId, Boolean paid, String rangeStart,
                                          String rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                          Integer size, HttpServletRequest request);

    EventFullDto getEventByPublic(Long eventId, HttpServletRequest request);
}
