package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> createHit(@RequestBody EndpointHitDto endpointHitDto) {
        return new ResponseEntity<>(statsService.create(endpointHitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam
                                       @DateTimeFormat(pattern = dateTimeFormat) LocalDateTime start,
                                       @RequestParam
                                       @DateTimeFormat(pattern = dateTimeFormat) LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        return ResponseEntity.ok().body(statsService.getStats(start, end, uris, unique));
    }
}
