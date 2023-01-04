package ru.practicum.participation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.ParticipationRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationMapper {
    public static ParticipationRequest mapParticipationRequestFromParticipationRequestDto(
            ParticipationRequestDto participationRequestDto) {
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(participationRequestDto.getCreated());
        participationRequest.setEvent(participationRequestDto.getEvent());
        participationRequest.setRequester(participationRequestDto.getRequester());
        participationRequest.setStatus(participationRequestDto.getStatus());
        return participationRequest;
    }

    public static ParticipationRequestDto mapParticipationRequestDtoFromParticipationRequest(
            ParticipationRequest participationRequest) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setCreated(participationRequest.getCreated());
        participationRequestDto.setEvent(participationRequest.getEvent());
        participationRequestDto.setRequester(participationRequest.getRequester());
        participationRequestDto.setStatus(participationRequest.getStatus());
        participationRequestDto.setId(participationRequest.getId());
        return participationRequestDto;
    }
}
