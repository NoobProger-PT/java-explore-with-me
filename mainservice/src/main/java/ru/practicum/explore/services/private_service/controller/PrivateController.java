package ru.practicum.explore.services.private_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.dto.NewEventDto;
import ru.practicum.explore.event.dto.UpdateEventDto;
import ru.practicum.explore.exception.event_exception.WrongEventDateException;
import ru.practicum.explore.participation.dto.ParticipationRequestDto;
import ru.practicum.explore.services.private_service.service.PrivateService;

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
public class PrivateController {

    private final PrivateService service;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAllByUserId(@PathVariable @Positive Long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get events of user with id: " + userId);
        return service.getByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getByUserIdAndEventId(@PathVariable @Positive Long eventId,
                                              @PathVariable @Positive Long userId) {
        log.info("Get event with id: " + eventId + ", and user with id: " + userId);
        return service.getByUserIdAndEventId(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationByEventId(@PathVariable @Positive Long eventId,
                                                                    @PathVariable @Positive Long userId) {
        log.info("Get participation of event id: " + eventId);
        return service.getParticipation(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getParticipationByUserId(@PathVariable @Positive Long userId) {
        log.info("Get all participation of user with id: " + userId);
        return service.getParticipationByUserId(userId);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto add(@PathVariable @Positive Long userId,
                            @RequestBody @Valid NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("Дата и время начала ивента не может быть раньше 2-х часов от" +
                    " текущего времени. Текущее время и дата: " + LocalDateTime.now() + ". Дата и время создаваемого " +
                    "ивента: " + newEventDto.getEventDate());
        }
        log.info("Add new Event with title: " + newEventDto.getTitle());
        return service.addEvent(newEventDto, userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto addParticipation(@PathVariable @Positive Long userId,
                                                    @RequestParam @Positive Long eventId) {
        log.info("Add new participation");
        return service.addParticipation(userId, eventId);
    }

    @PatchMapping("/{userId}/events")
    public EventFullDto update(@PathVariable @Positive Long userId,
                               @RequestBody @Valid UpdateEventDto updateEventDto) {
        if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("Дата и время начала ивента не может быть раньше 2-х часов от" +
                    " текущего времени. Текущее время и дата: " + LocalDateTime.now() + ". Дата и время создаваемого " +
                    "ивента: " + updateEventDto.getEventDate());
        }
        log.info("Update Event with id: " + updateEventDto.getEventId());
        return service.updateEvent(updateEventDto, userId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto cancel(@PathVariable @Positive Long eventId,
                               @PathVariable @Positive Long userId) {
        log.info("Cancel Event with id: " + eventId);
        return service.cancelEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmReq(@PathVariable @Positive Long eventId,
                                              @PathVariable @Positive Long userId,
                                              @PathVariable @Positive Long reqId) {
        log.info("Confirm participation with id: " + reqId);
        return service.confirmParticipation(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectReq(@PathVariable @Positive Long eventId,
                                           @PathVariable @Positive Long userId,
                                           @PathVariable @Positive Long reqId) {
        log.info("Reject participation with id: " + reqId);
        return service.rejectParticipation(userId, eventId, reqId);
    }

    @PatchMapping("/{userId}/requests/{requestsId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long requestsId) {
        log.info("Cancel participation with id: " + requestsId);
        return service.cancelRequest(userId, requestsId);
    }
}
