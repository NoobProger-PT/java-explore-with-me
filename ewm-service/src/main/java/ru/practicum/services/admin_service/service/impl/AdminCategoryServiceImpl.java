package ru.practicum.services.admin_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CantDeleteCategoryException;
import ru.practicum.exception.category_exception.CategoryAlreadyExistsException;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.service.AdminCategoryService;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final AdminCategoryRepository categoryRepository;

    private final PrivateEventsRepository eventsRepository;

    @Override
    @Transactional
    public CategoryDto changeCategory(CategoryDto categoryDto) {
        Category category = checkCategory(categoryDto.getId());
        try {
            category.setName(categoryDto.getName());
            return CategoryMapper.mapToCategoryDto(category);
        } catch (CategoryAlreadyExistsException e) {
            throw new CategoryAlreadyExistsException("Новая категория уже имеется в списке.");
        }
    }

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        try {
            Category category = categoryRepository.save(CategoryMapper.mapToCategory(categoryDto));
            return CategoryMapper.mapToCategoryDto(category);
        } catch (CategoryAlreadyExistsException e) {
            throw new CategoryAlreadyExistsException("Новая категория уже имеется в списке.");
        }
    }

    @Override
    @Transactional
    public String deleteCategory(long catId) {
        checkCategory(catId);
        List<Event> events = eventsRepository.findAllByCategoryId(catId);
        if (events.size() != 0) {
            throw new CantDeleteCategoryException("Удаляемая категория присутствует в событиях: " + events);
        } else {
            categoryRepository.deleteById(catId);
            return "Категория удалена";
        }
    }

    private Category checkCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new CategoryNotFoundException("Категория с id: " + categoryId + " не найдена"));
        return category;
    }
}
