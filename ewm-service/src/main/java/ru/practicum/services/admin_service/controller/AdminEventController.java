package ru.practicum.services.admin_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.services.admin_service.service.AdminEventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
@Validated
public class AdminEventController {

    private final AdminEventService service;

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(defaultValue = "List.of()") List<Long> users,
                                        @RequestParam(defaultValue = "List.of()") List<String> states,
                                        @RequestParam(defaultValue = "List.of()") List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос админа на получение событий");
        return service.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive long eventId,
                                    @RequestBody @Valid AdminUpdateEventRequest adminUpdateEventRequest) {
        log.info("Получен запрос админа на редактирование события");
        return service.updateEvent(eventId, adminUpdateEventRequest);
    }

    @PatchMapping("/events/{eventId}/publish")
    public EventFullDto publishEvent(@PathVariable @Positive long eventId) {
        log.info("Админ публикует событие: {}", eventId);
        return service.publishEvent(eventId);
    }

    @PatchMapping("/events/{eventId}/reject")
    public EventFullDto rejectEvent(@PathVariable @Positive long eventId) {
        log.info("Админ отклоняет событие: {}", eventId);
        return service.rejectEvent(eventId);
    }
}
