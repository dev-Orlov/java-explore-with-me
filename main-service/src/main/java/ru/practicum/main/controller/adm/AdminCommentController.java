package ru.practicum.main.controller.adm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Valid @Positive @RequestParam(defaultValue = "10")
                                                        Integer size) {
        return ResponseEntity.ok().body(commentService.getComments(from, size));
    }
}
