package ru.practicum.main.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long catId) {
        return ResponseEntity.ok().body(categoryService.getCategory(catId));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@PositiveOrZero @RequestParam(defaultValue = "0")
                                                               Integer from,
                                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(categoryService.getCategories(from, size));
    }
}
