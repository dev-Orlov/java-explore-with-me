package ru.practicum.main.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.service.CommentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> create(@PathVariable Long userId,
                                             @RequestBody @Valid NewCommentDto newCommentDto) {
        return new ResponseEntity<>(commentService.create(userId, newCommentDto), HttpStatus.CREATED);
    }

    @PatchMapping("{commentId}")
    public ResponseEntity<CommentDto> updateCommentByOwner(@PathVariable Long userId, @PathVariable Long commentId,
                                                           @RequestBody @Valid NewCommentDto newCommentDto) {
        return ResponseEntity.ok().body(commentService.updateCommentByOwner(userId, commentId, newCommentDto));
    }
}
