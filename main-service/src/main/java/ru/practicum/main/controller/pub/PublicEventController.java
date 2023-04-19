package ru.practicum.main.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.EventFullDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsByPublic(@RequestParam(required = false) String text,
                                                                 @RequestParam(name = "categories",
                                                                         required = false) List<Long> categoriesId,
                                                                 @RequestParam(required = false) Boolean paid,
                                                                 @RequestParam(required = false) String rangeStart,
                                                                 @RequestParam(required = false) String rangeEnd,
                                                                 @RequestParam(defaultValue = "false")
                                                                     Boolean onlyAvailable,
                                                                 @RequestParam(required = false) String sort,
                                                                 @PositiveOrZero @RequestParam(defaultValue = "0")
                                                                     int from,
                                                                 @Positive @RequestParam(defaultValue = "10") int size,
                                                                 HttpServletRequest request) {
        return ResponseEntity.ok().body(eventService.getEventsByPublic(text, categoriesId, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventByPublic(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok().body(eventService.getEventByPublic(id, request));
    }

}
