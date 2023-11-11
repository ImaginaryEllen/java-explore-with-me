package ru.practicum.ewm.service.category;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.CategoryRequestDto;

import java.util.List;

public interface CategoryService {
    CategoryDto saveCategory(CategoryRequestDto requestDto);

    void deleteCategoryById(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    List<CategoryDto> getAllCategories(Pageable page);

    CategoryDto getCategoryById(Long catId);
}
