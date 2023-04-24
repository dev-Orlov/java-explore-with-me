package ru.practicum.main.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Valid @Positive @RequestParam(defaultValue = "10")
                                                        Integer size) {
        return ResponseEntity.ok().body(commentService.getComments(from, size));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long commentId) {
        return ResponseEntity.ok().body(commentService.getCommentById(commentId));
    }
}
