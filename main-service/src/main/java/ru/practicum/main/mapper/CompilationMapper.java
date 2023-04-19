package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.repository.EventRepository;

@Mapper(componentModel = "spring")
public abstract class CompilationMapper {

    @Autowired
    protected EventRepository eventRepository;

    public abstract CompilationDto compilationToCompilationDto(Compilation compilation);

    @Mapping(target = "events",
            expression = "java(eventRepository.findAllById(newCompilationDto.getEvents()))")
    public abstract Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto);
}
