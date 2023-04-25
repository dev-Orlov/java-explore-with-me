package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.dto.NewCommentDto;
import ru.practicum.main.exception.IncorrectCommentException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CommentMapper;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.User;
import ru.practicum.main.model.event.Event;
import ru.practicum.main.model.event.EventState;
import ru.practicum.main.repository.CommentRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.service.CommentService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto create(Long userId, NewCommentDto newCommentDto) {
        User user = getUser(userId);
        Event event = getEvent(newCommentDto.getEvent());
        checkEventState(event);
        boolean isOrganizer = false;
        String displayName = user.getName();

        if (event.getInitiator().getId().equals(userId)) {
            isOrganizer = true;
            displayName = event.getTitle();
        }

        Comment comment = new Comment(null, newCommentDto.getText(), event, user,
                LocalDateTime.now().withNano(0), isOrganizer, displayName);

        commentRepository.save(comment);

        CommentDto commentDto = commentMapper.commentToCommentDto(comment);

        log.debug("Сохранён объект комментария {}", comment);
        return commentDto;
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found."));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found."));
    }

    private void checkEventState(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IncorrectCommentException("Мероприятие недоступно для комментирования");
        }
    }

    @Override
    @Transactional
    public CommentDto updateCommentByOwner(Long userId, Long commentId, NewCommentDto newCommentDto) {
        getUser(userId);
        Event event = getEvent(newCommentDto.getEvent());
        checkEventState(event);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found."));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new IncorrectCommentException("Нельзя отредактировать чужой комментарий");
        }

        if (comment.getCreated().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new IncorrectCommentException("Редактировать свой комментарий можно только в течение суток" +
                    " после публикации");
        }

        comment.setText(newCommentDto.getText());
        comment = commentRepository.save(comment);

        log.debug("Обновлён объект комментария: {}", event);
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found."));

        commentRepository.deleteById(commentId);
        log.debug("Удалён объект комментария: {}", comment);
    }

    @Override
    public List<CommentDto> getComments(int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<CommentDto> commentList = commentRepository.findAll(page).stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toList());

        log.debug("Получен список комментариев");
        return commentList;
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found."));

        log.debug("Получен комментарий с id={}", commentId);
        return commentMapper.commentToCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsForEvent(Long eventId) {
        getEvent(eventId);
        List<Comment> eventComments = commentRepository.findAllByEventId(eventId);

        log.debug("Получен список комментариев для события с id={}", eventId);
        return eventComments.stream().map(commentMapper::commentToCommentDto)
                .collect(Collectors.toList());
    }
}
