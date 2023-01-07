package ru.practicum.services.admin_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Marker;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.services.admin_service.service.AdminCategoryService;

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
@Validated
public class AdminCategoryController {

    private final AdminCategoryService service;

    @PostMapping("/categories")
    public CategoryDto add(@RequestBody @Validated({Marker.Create.class}) CategoryDto categoryDto) {
        log.info("Add category with name: {}", categoryDto.getName());
        return service.addCategory(categoryDto);
    }

    @PatchMapping("/categories")
    public CategoryDto change(@RequestBody @Validated({Marker.Update.class}) CategoryDto categoryDto) {
        log.info("Change category with id: {}", categoryDto.getId());
        return service.changeCategory(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public String delete(@PathVariable @Positive long catId) {
        log.info("Delete Category with Id: {}", catId);
        return service.deleteCategory(catId);
    }
}
