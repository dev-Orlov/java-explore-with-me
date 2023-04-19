package ru.practicum.main.service;

import ru.practicum.main.dto.ParticipationRequestDto;

import javax.transaction.Transactional;
import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequests(Long userId);

    @Transactional
    ParticipationRequestDto create(Long userId, Long eventId);

    @Transactional
    ParticipationRequestDto cancel(Long userId, Long requestId);
}
