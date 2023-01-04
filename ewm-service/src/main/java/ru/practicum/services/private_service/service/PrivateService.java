package ru.practicum.services.private_service.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateService {

    EventFullDto addEvent(NewEventDto newEventDto, long userId);

    List<EventShortDto> getByUserId(long userId, int from, int size);

    EventFullDto updateEvent(UpdateEventDto updateEventDto, long userId);

    EventFullDto getByUserIdAndEventId(long userId, long eventId);

    EventFullDto cancelEvent(long userId, long eventId);

    List<ParticipationRequestDto> getParticipation(long userId, long eventId);

    ParticipationRequestDto confirmParticipation(long userId, long eventId, long reqId);

    ParticipationRequestDto rejectParticipation(long userId, long eventId, long reqId);

    List<ParticipationRequestDto> getParticipationByUserId(long userId);

    ParticipationRequestDto addParticipation(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
