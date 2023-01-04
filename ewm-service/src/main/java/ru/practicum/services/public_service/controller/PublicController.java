package ru.practicum.services.public_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.services.public_service.service.PublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {

    private final PublicService service;

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(required = false) String text,
                                        @RequestParam(defaultValue = "List.of()") List<Long> categories,
                                        @RequestParam(required = false) boolean paid,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        log.info("Получение событий по входным данным");
        return service.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from,
                size, request);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable @Positive long id, HttpServletRequest request) {
        log.info("Получение события по ID");
        return service.getEventById(id, request);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(required = false) boolean pinned,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        log.info("Получение подборки событий");
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{complId}")
    public CompilationDto getCompilationById(@PathVariable @Positive long complId) {
        log.info("Получение подборки событий по Id");
        return service.getCompilationById(complId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("Получение категорий");
        return service.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable @Positive long catId) {
        log.info("Получение категории по Id");
        return service.getCategory(catId);
    }
}
