package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.main.model.request.ParticipationRequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;
    private ParticipationRequestStatus status;
}
