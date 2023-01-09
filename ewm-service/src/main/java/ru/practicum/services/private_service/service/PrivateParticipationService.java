package ru.practicum.services.private_service.service;

import ru.practicum.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateParticipationService {

    List<ParticipationRequestDto> getParticipation(long userId, long eventId);

    ParticipationRequestDto confirmParticipation(long userId, long eventId, long reqId);

    ParticipationRequestDto rejectParticipation(long userId, long eventId, long reqId);

    List<ParticipationRequestDto> getParticipationByUserId(long userId);

    ParticipationRequestDto addParticipation(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
