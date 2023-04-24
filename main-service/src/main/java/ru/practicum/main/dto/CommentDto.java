package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class CommentDto {

    private Long id;
    @NotBlank
    private String text;
    @NotNull
    private EventShortDto event;
    @NotNull
    private UserShortDto author;
    private String created;
    private Boolean organizer;
    private String displayName;
}
