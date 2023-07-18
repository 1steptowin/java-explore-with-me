package main.server.service.category;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import main.server.dto.category.CategoryDto;
import main.server.dto.category.NewCategoryDto;
import main.server.exception.category.CategoryNotFoundException;
import main.server.mapper.category.CategoryMapper;
import main.server.model.category.Category;
import main.server.repo.category.CategoryRepo;
import main.server.service.EWMPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService{
    CategoryRepo categoryRepo;

    @Autowired
    public CategoryServiceImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto category) {
        return CategoryMapper.mapModelToDto(categoryRepo.save(CategoryMapper.mapDtoToModel(category)));
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto inputCategory) {
        Category category = categoryRepo.findById(catId).orElseThrow(() -> {
            throw new CategoryNotFoundException("Category does not exist");
        });
        category.setName(inputCategory.getName());
        return CategoryMapper.mapModelToDto(categoryRepo.save(category));
    }

    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        checkIfCategoryNotExists(catId);
        categoryRepo.deleteById(catId);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        return categoryRepo.findAll(new EWMPageRequest(from, size)).getContent().stream()
                .map(CategoryMapper::mapModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        checkIfCategoryNotExists(catId);
        return CategoryMapper.mapModelToDto(categoryRepo.findById(catId).orElseThrow());
    }

    private void checkIfCategoryNotExists(Long catId) {
        if (categoryRepo.findById(catId).isEmpty()) {
            throw new CategoryNotFoundException("Category does not exist");
        }
    }
}
