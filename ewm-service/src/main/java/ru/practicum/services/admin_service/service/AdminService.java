package ru.practicum.services.admin_service.service;


import ru.practicum.category.dto.CategoryDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface AdminService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto addUser(NewUserDto newUserDto);

    String delete(long userId);

    CategoryDto changeCategory(CategoryDto categoryDto);

    CategoryDto addCategory(CategoryDto categoryDto);

    String deleteCategory(long catId);

    List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories,
                                 String rangeStart, String rangeEnd, int from, int size);

    EventFullDto updateEvent(long eventId, AdminUpdateEventRequest adminUpdateEventRequest);

    EventFullDto publishEvent(long eventId);

    EventFullDto rejectEvent(long eventId);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    String deleteCompilationById(long complId);

    String deleteEventFromCompilation(long eventId, long complId);

    CompilationDto addEventIntoCompilation(long complId, long eventId);

    CompilationDto removeCompilationFromMainPage(long complId);

    CompilationDto addCompilationOnMainPage(long complId);
}
