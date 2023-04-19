package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.CompilationDto;
import ru.practicum.main.dto.NewCompilationDto;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.model.Compilation;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.service.CompilationService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<CompilationDto> compilationList;
        Pageable page = PageRequest.of(from, size);

        if (pinned == null) {
            compilationList = compilationRepository.findAll(page).stream()
                    .map(compilationMapper::compilationToCompilationDto).collect(Collectors.toList());
            log.debug("Получен полный список подборок событий");
            return compilationList;
        } else if (pinned) {
            compilationList = compilationRepository.findAllByPinned(pinned, page).stream()
                    .map(compilationMapper::compilationToCompilationDto).collect(Collectors.toList());
            log.debug("Получен список закрепленных подборок событий");
            return compilationList;
        } else {
            compilationList = compilationRepository.findAllByPinned(pinned, page).stream()
                    .map(compilationMapper::compilationToCompilationDto).collect(Collectors.toList());
            log.debug("Получен список незакрепленных подборок событий");
            return compilationList;
        }
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found."));

        log.debug("Получен объект подборки событий {}", compilation);
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);
        compilation = compilationRepository.save(compilation);

        log.debug("Сохранён объект подборки событий: {}", compilation);
        return compilationMapper.compilationToCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto delete(Long compId) {
        CompilationDto compilationDto = getCompilationById(compId);
        compilationRepository.deleteById(compId);

        log.debug("Удалён объект подборки событий с id={}", compId);
        return compilationDto;
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found."));
        Compilation newCompilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);

        if (newCompilation.getPinned() != null) {
            compilation.setPinned(newCompilation.getPinned());
        }
        if (newCompilation.getTitle() != null) {
            compilation.setTitle(newCompilation.getTitle());
        }
        if (newCompilation.getEvents() != null) {
            compilation.setEvents(newCompilation.getEvents());
        }

        compilationRepository.save(compilation);
        log.debug("Обновлён объект подборки событий: {}", compilation);
        return compilationMapper.compilationToCompilationDto(compilation);
    }
}
