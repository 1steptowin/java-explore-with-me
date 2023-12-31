package main.server.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.category.CategoryDto;
import main.server.dto.category.NewCategoryDto;
import main.server.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(value = PathsConstants.CATEGORY_ADMIN_PATH)
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(category));
    }

    @PatchMapping(value = PathsConstants.CATEGORY_ADMIN_BY_ID_PATH)
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable("catId") Long catId, @Valid @RequestBody CategoryDto category) {
        return ResponseEntity.ok().body(categoryService.updateCategory(catId, category));
    }

    @DeleteMapping(value = PathsConstants.CATEGORY_ADMIN_BY_ID_PATH)
    public ResponseEntity<Void> deleteCategory(@PathVariable("catId") Long catId) {
        categoryService.deleteCategory(catId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = PathsConstants.CATEGORY_PUBLIC_PATH)
    public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(required = false, defaultValue = "0") int from,
                                                              @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok().body(categoryService.getAllCategories(from, size));
    }

    @GetMapping(PathsConstants.CATEGORY_PUBLIC_BY_ID_PATH)
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable("catId") Long catId) {
        return ResponseEntity.ok().body(categoryService.getCategoryById(catId));
    }
}
