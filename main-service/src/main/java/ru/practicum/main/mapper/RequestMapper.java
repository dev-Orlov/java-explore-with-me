package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.main.dto.ParticipationRequestDto;
import ru.practicum.main.model.request.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "created", expression = "java(participationRequest.getCreated().toString())")
    @Mapping(target = "event", expression = "java(participationRequest.getEvent().getId())")
    @Mapping(target = "requester", expression = "java(participationRequest.getRequester().getId())")
    ParticipationRequestDto participationRequestToParticipationRequestDto(ParticipationRequest participationRequest);
}
