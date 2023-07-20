package main.server.service.category;

import main.server.dto.category.CategoryDto;
import main.server.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto category);

    CategoryDto updateCategory(Long catId, CategoryDto category);

    void deleteCategory(Long catId);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(Long catId);
}
