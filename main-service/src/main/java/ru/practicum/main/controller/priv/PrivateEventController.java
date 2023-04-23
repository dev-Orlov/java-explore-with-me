package ru.practicum.main.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.*;
import ru.practicum.main.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(@PathVariable Long userId,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                         @Positive @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(eventService.getEvents(userId, from, size));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> create(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return new ResponseEntity<>(eventService.create(userId, newEventDto), HttpStatus.CREATED);
    }

    @GetMapping("{eventId}")
    public ResponseEntity<EventFullDto> getEventByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        return ResponseEntity.ok().body(eventService.getEventByOwner(userId, eventId));
    }

    @PatchMapping("{eventId}")
    public ResponseEntity<EventFullDto> updateEventByOwner(@PathVariable Long userId, @PathVariable Long eventId,
                                        @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {
        return ResponseEntity.ok().body(eventService.updateEventByOwner(userId, eventId, updateEventUserRequestDto));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequestsByOwner(@PathVariable Long userId,
                                                                               @PathVariable Long eventId) {
        return ResponseEntity.ok().body(eventService.getEventRequestsByOwner(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateEventRequestsByOwner(@PathVariable Long userId,
                                                                @PathVariable Long eventId,
                                                                @RequestBody EventRequestStatusUpdateRequest requests) {
        return ResponseEntity.ok().body(eventService.updateEventRequestsByOwner(userId, eventId, requests));
    }
}
