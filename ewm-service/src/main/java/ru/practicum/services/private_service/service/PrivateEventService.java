package ru.practicum.services.private_service.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;

import java.util.List;

public interface PrivateEventService {

    EventFullDto addEvent(NewEventDto newEventDto, long userId);

    List<EventShortDto> getByUserId(long userId, int from, int size);

    EventFullDto updateEvent(UpdateEventDto updateEventDto, long userId);

    EventFullDto getByUserIdAndEventId(long userId, long eventId);

    EventFullDto cancelEvent(long userId, long eventId);
}
