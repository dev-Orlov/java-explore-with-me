package ru.practicum.main.controller.adm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.CategoryDto;
import ru.practicum.main.dto.NewCategoryDto;
import ru.practicum.main.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        return new ResponseEntity<>(categoryService.create(newCategoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId ) {
        categoryService.delete(catId);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> update(@PathVariable @NotNull Long catId,
                                              @Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok().body(categoryService.update(categoryDto, catId));
    }
}
