package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.NewEventDto;
import ru.practicum.main.model.event.Event;
import ru.practicum.main.repository.CategoryRepository;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected CategoryMapper categoryMapper;
    @Autowired
    protected CategoryRepository categoryRepository;

    @Mapping(target = "category",
            expression = "java(categoryMapper.categoryToCategoryDto(event.getCategory()))")
    @Mapping(target = "eventDate",
            expression = "java(event.getEventDate().toString())")
    @Mapping(target = "initiator",
            expression = "java(userMapper.userToUserShortDto(event.getInitiator()))")
    public abstract EventShortDto eventToEventShortDto(Event event);

    @Mapping(target = "category",
            expression = "java(categoryRepository.findById(newEventDto.getCategory()).get())")
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    public abstract Event newEventDtoToEvent(NewEventDto newEventDto);

    @Mapping(target = "category",
            expression = "java(categoryMapper.categoryToCategoryDto(event.getCategory()))")
    @Mapping(target = "createdOn",
            expression = "java(event.getCreateOn().toString())")
    @Mapping(target = "eventDate",
            expression = "java(event.getEventDate().toString())")
    @Mapping(target = "initiator",
            expression = "java(userMapper.userToUserShortDto(event.getInitiator()))")
    public abstract EventFullDto eventToEventFullDto(Event event);
}
