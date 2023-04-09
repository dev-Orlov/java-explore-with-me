package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.mapper.EndpointHitMapper;
import ru.practicum.stats.server.mapper.ViewStatsMapper;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;
    private final EndpointHitRepository repository;

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = repository.save(endpointHitMapper.endpointHitDtoToEndpointHit(endpointHitDto));

        log.debug("Сохранён объект просмотра: {}", endpointHit);
        return endpointHitMapper.endpointHitToEndpointHitDto(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStatsDto> resultList;  // итоговый список посещений

        if (unique) {
            resultList = repository.getUniqueViews(start, end, uris).stream()
                    .map(viewStatsMapper::viewStatsToViewStatsDto).collect(Collectors.toList());

            log.debug("Получен список уникальных посещений");
        } else {
            resultList = repository.getAllViews(start, end, uris).stream()
                    .map(viewStatsMapper::viewStatsToViewStatsDto).collect(Collectors.toList());

            log.debug("Получен полный список посещений");
        }
        return resultList;
    }
}
