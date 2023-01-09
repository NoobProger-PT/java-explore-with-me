package ru.practicum.services.public_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.public_service.service.PublicCategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCategoryServiceImpl implements PublicCategoryService {

    private final AdminCategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        List<CategoryDto> result = categoryRepository.findAll(
                        PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public CategoryDto getCategory(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new CategoryNotFoundException("категория не найдена"));
        return CategoryMapper.mapToCategoryDto(category);
    }
}
