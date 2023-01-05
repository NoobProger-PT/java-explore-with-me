package ru.practicum.services.public_service.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
