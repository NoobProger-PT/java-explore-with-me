package ru.practicum.services.admin_service.service;

import ru.practicum.category.dto.CategoryDto;

public interface AdminCategoryService {

    CategoryDto changeCategory(CategoryDto categoryDto);

    CategoryDto addCategory(CategoryDto categoryDto);

    String deleteCategory(long catId);
}
