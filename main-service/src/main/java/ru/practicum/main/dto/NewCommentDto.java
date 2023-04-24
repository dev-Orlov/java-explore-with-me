package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class NewCommentDto {

    @NotBlank
    private String text;
    @NotNull
    private Long event;
}
