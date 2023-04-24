package ru.practicum.main.service;

import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;

import javax.transaction.Transactional;
import java.util.List;

public interface CommentService {
    @Transactional
    CommentDto create(Long userId, NewCommentDto newCommentDto);

    @Transactional
    CommentDto updateCommentByOwner(Long userId, Long commentId, NewCommentDto newCommentDto);

    @Transactional
    void delete(Long commentId);

    List<CommentDto> getComments(int from, int size);

    CommentDto getCommentById(Long commentId);
}
