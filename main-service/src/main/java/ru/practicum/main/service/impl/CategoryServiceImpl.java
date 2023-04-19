package ru.practicum.main.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.EventShortDto;
import ru.practicum.main.dto.NewCategoryDto;
import ru.practicum.main.exception.AlreadyExistsException;
import ru.practicum.main.exception.IncorrectEventException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Category;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryRepository.save(categoryMapper.newCategoryDtoToCategory(newCategoryDto));

            log.debug("Сохранён объект категории: {}", category);
            return categoryMapper.categoryToCategoryDto(category);

        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("имя категории " + newCategoryDto.getName() + " должно быть уникальным");
        }
    }

    @Override
    @Transactional
    public CategoryDto remove(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found."));
        CategoryDto categoryDto = categoryMapper.categoryToCategoryDto(category);

        List<EventShortDto> checkEvents = eventRepository.findByCategoryId(categoryId).stream()
                .map(eventMapper::eventToEventShortDto).collect(Collectors.toList());

        if (checkEvents.size() != 0) {
            throw new IncorrectEventException("C категорией не должно быть связано ни одного события");
        }
        categoryRepository.deleteById(categoryId);

        log.debug("Удалён объект категории: {}", category);
        return categoryDto;
    }

    @Override
    @Transactional
    public CategoryDto update(CategoryDto categoryDto, Long categoryId) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new AlreadyExistsException("имя категории " + categoryDto.getName() + " должно быть уникальным");
        }

        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new NotFoundException("Попытка обновить несуществующую категорию");
        }

        Category category = categoryRepository.findById(categoryId).get();
        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);

        log.debug("Обновлён объект категории: {}", category);
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found."));

        log.debug("Получена категория с id={}", catId);
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);

        List<CategoryDto> categoryList = categoryRepository.findAll(page).stream()
                .map(categoryMapper::categoryToCategoryDto).collect(Collectors.toList());

        log.debug("Получен список категорий");
        return categoryList;
    }
}
