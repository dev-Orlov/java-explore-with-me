package ru.practicum.main.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long commentId) {
        return ResponseEntity.ok().body(commentService.getCommentById(commentId));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsForEvent(@RequestParam Long eventId) {
        return ResponseEntity.ok().body(commentService.getCommentsForEvent(eventId));
    }
}
