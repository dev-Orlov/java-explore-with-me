package ru.practicum.main.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;
import ru.practicum.main.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto categoryToCategoryDto(Category category);

    Category categoryDtoToCategory(CategoryDto categoryDto);

    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);
}
