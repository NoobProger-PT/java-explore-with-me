package ru.practicum.services.public_service.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.event.dto.EventFullDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PublicService {

    List<EventFullDto> getEvents(String text, List<Long> categories, boolean paid, String rangeStart,
                                 String rangeEnd, boolean onlyAvailable, String sort, int from, int size,
                                 HttpServletRequest request);

    EventFullDto getEventById(long id, HttpServletRequest request);

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);

    CompilationDto getCompilationById(long complId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long catId);
}
