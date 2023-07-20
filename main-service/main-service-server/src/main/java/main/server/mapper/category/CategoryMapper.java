package main.server.mapper.category;

import main.server.dto.category.CategoryDto;
import main.server.dto.category.NewCategoryDto;
import main.server.model.category.Category;

public class CategoryMapper {
    public static Category mapDtoToModel(NewCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }

    public static CategoryDto mapModelToDto(Category model) {
        return CategoryDto.builder()
                .id(model.getCategoryId())
                .name(model.getName())
                .build();
    }
}
