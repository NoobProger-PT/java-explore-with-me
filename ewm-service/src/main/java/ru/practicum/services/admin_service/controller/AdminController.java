package ru.practicum.services.admin_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Marker;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.services.admin_service.service.AdminService;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
@Validated
public class AdminController {

    private final AdminService service;

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(defaultValue = "-1") List<Long> ids,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get Users with Ids: " + ids);
        return service.getUsers(ids, from, size);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(defaultValue = "List.of()") List<Long> users,
                                        @RequestParam(defaultValue = "List.of()") List<String> states,
                                        @RequestParam(defaultValue = "List.of()") List<Long> categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос админа на получение событий");
        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PostMapping("/users")
    public UserDto addUser(@RequestBody @Valid NewUserDto newUserDto) {
        log.info("Add new User with name: " + newUserDto.getName() + " and Email: " + newUserDto.getEmail());
        return service.addUser(newUserDto);
    }

    @PostMapping("/categories")
    public CategoryDto addCategory(@RequestBody @Validated({Marker.Create.class}) CategoryDto categoryDto) {
        log.info("Add category with name: " + categoryDto.getName());
        return service.addCategory(categoryDto);
    }

    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен запрос на добавление новой подборки");
        return service.addCompilation(newCompilationDto);
    }

    @PutMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive long eventId,
                                    @RequestBody @Valid AdminUpdateEventRequest adminUpdateEventRequest) {
        log.info("Получен запрос админа на редактирование события");
        return service.updateEvent(eventId, adminUpdateEventRequest);
    }

    @PatchMapping("/categories")
    public CategoryDto changeCategory(@RequestBody @Validated({Marker.Update.class}) CategoryDto categoryDto) {
        log.info("Change category with id: " + categoryDto.getId());
        return service.changeCategory(categoryDto);
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable @Positive long eventId) {
        log.info("Админ публикует событие: " + eventId);
        return service.publishEvent(eventId);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable @Positive long eventId) {
        log.info("Админ отклоняет событие: " + eventId);
        return service.rejectEvent(eventId);
    }

    @PatchMapping("/compilations/{complId}/events/{eventId}")
    public CompilationDto addEventIntoCompilation(@PathVariable @Positive long complId,
                                                  @PathVariable @Positive long eventId) {
        log.info("Добавление события в подборку");
        return service.addEventIntoCompilation(complId, eventId);
    }

    @PatchMapping("/compilations/{complId}/pin")
    public CompilationDto addCompilationOnMainPage(@PathVariable @Positive long complId) {
        log.info("Закрепления подборки на главной странице");
        return service.addCompilationOnMainPage(complId);
    }

    @DeleteMapping("/users/{userId}")
    public String delete(@PathVariable @Positive long userId) {
        log.info("Delete User with Id: " + userId);
        return service.delete(userId);
    }

    @DeleteMapping("/categories/{catId}")
    public String deleteCategory(@PathVariable @Positive long catId) {
        log.info("Delete Category with Id: " + catId);
        return service.deleteCategory(catId);
    }

    @DeleteMapping("/compilations/{complId}")
    public String deleteCompilation(@PathVariable @Positive long complId) {
        log.info("Удаление подборки");
        return service.deleteCompilationById(complId);
    }

    @DeleteMapping("/compilations/{complId}/events/{eventId}")
    public String deleteEventFromCompilation(@PathVariable @Positive long eventId,
                                           @PathVariable @Positive long complId) {
        log.info("Удаление события из подборки");
        return service.deleteEventFromCompilation(complId, eventId);
    }

    @DeleteMapping("/compilations/{complId}/pin")
    public CompilationDto removeCompilationFromMainPage(@PathVariable @Positive long complId) {
        log.info("Убрать подборку из главной страницы");
        return service.removeCompilationFromMainPage(complId);
    }
}
