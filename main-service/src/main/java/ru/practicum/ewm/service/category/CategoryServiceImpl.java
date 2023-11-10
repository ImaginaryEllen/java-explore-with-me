package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.CategoryRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto saveCategory(CategoryRequestDto requestDto) {
        Category byName = categoryRepository.findByName(requestDto.getName());
        if (byName != null) {
            throw new ConflictException("Category name: " + requestDto.getName() + " is already taken");
        }
        Category category = categoryRepository.save(CategoryMapper.toCategory(requestDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        List<Event> events = eventRepository.findAllByCategory_Id(catId);
        if (events.size() == 0) {
            categoryRepository.delete(category);
        } else {
            throw new ConflictException("The events using category with id=" + catId);
        }
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAllCategories(Pageable page) {
        List<Category> categories = categoryRepository.findAll(page).toList();
        if (categories.size() > 0) {
            return categories.stream()
                    .map(CategoryMapper::toCategoryDto)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        return CategoryMapper.toCategoryDto(category);
    }
}
