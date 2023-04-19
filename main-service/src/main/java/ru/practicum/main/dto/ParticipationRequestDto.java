package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.main.model.request.ParticipationRequestStatus;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {

    private Long id;
    private String created;
    private Long event;
    private Long requester;
    private ParticipationRequestStatus status;
}
