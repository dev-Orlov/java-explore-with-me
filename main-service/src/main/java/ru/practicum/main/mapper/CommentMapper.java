package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.main.dto.CommentDto;
import ru.practicum.main.model.Comment;

import java.time.format.DateTimeFormatter;


@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Autowired
    protected UserMapper userMapper;

    @Autowired
    protected EventMapper eventMapper;

    protected static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "author",
            expression = "java(userMapper.userToUserShortDto(comment.getAuthor()))")
    @Mapping(target = "event",
            expression = "java(eventMapper.eventToEventShortDto(comment.getEvent()))")
    @Mapping(target = "created",
            expression = "java(comment.getCreated().format(dateTimeFormatter))")
    public abstract CommentDto commentToCommentDto(Comment comment);

}
