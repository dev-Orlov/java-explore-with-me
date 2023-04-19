package ru.practicum.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class CategoryDto {

    private Long id;
    @NotBlank
    private String name;
}
