package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
public class CompilationDto {

    private Long id;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
    private Set<EventShortDto> events;
}
