package ru.practicum.main.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    @Transactional
    CategoryDto create(NewCategoryDto newCategoryDto);

    @Transactional
    CategoryDto delete(Long categoryId);

    @Transactional
    CategoryDto update(CategoryDto categoryDto, Long categoryId);

    CategoryDto getCategory(Long catId);

    List<CategoryDto> getCategories(Integer from, Integer size);
}
