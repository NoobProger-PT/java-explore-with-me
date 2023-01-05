package ru.practicum.services.private_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.exception.event_exception.WrongEventDateException;
import ru.practicum.services.private_service.service.PrivateEventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class PrivateEventController {

    private final PrivateEventService service;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAllByUserId(@PathVariable @Positive Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get events of user with id: {}", userId);
        return service.getByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getByUserIdAndEventId(@PathVariable @Positive Long eventId,
                                              @PathVariable @Positive Long userId) {
        log.info("Get event with id: {}, and user with id: {}", eventId, userId);
        return service.getByUserIdAndEventId(userId, eventId);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto add(@PathVariable @Positive Long userId,
                            @RequestBody @Valid NewEventDto newEventDto) {
        checkDate(newEventDto.getEventDate());
        log.info("Add new Event with title: {}", newEventDto.getTitle());
        return service.addEvent(newEventDto, userId);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto update(@PathVariable @Positive Long userId,
                               @RequestBody @Valid UpdateEventDto updateEventDto) {
        checkDate(updateEventDto.getEventDate());
        log.info("Update Event with id: {}", updateEventDto.getEventId());
        return service.updateEvent(updateEventDto, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancel(@PathVariable @Positive Long eventId,
                               @PathVariable @Positive Long userId) {
        log.info("Cancel Event with id: {}", eventId);
        return service.cancelEvent(userId, eventId);
    }

    private void checkDate(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("Дата и время начала ивента не может быть раньше 2-х часов от" +
                    " текущего времени. Текущее время и дата: " + LocalDateTime.now() + ". Дата и время создаваемого " +
                    "ивента: " + date);
        }
    }
}
