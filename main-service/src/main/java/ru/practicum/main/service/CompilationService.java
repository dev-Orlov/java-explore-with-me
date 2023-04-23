package ru.practicum.main.service;

import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;

import javax.transaction.Transactional;
import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

    @Transactional
    CompilationDto create(NewCompilationDto newCompilationDto);

    @Transactional
    CompilationDto delete(Long compId);

    @Transactional
    CompilationDto update(Long compId, NewCompilationDto newCompilationDto);
}
