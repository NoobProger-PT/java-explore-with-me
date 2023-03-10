package ru.practicum.services.private_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.State;
import ru.practicum.event.model.Event;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.participation_exception.InvalidParticipationException;
import ru.practicum.exception.participation_exception.ParticipationAlreadyExistsException;
import ru.practicum.exception.participation_exception.ParticipationNotFoundException;
import ru.practicum.exception.user_exception.UserNotFound;
import ru.practicum.participation.Status;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.mapper.ParticipationMapper;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.services.admin_service.repository.AdminUsersRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.repository.PrivateParticipationRepository;
import ru.practicum.services.private_service.service.PrivateParticipationService;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateParticipationServiceImpl implements PrivateParticipationService {

    private final PrivateEventsRepository eventsRepository;
    private final AdminUsersRepository usersRepository;
    private final PrivateParticipationRepository participationRepository;

    @Override
    public List<ParticipationRequestDto> getParticipation(long userId, long eventId) {
        checkEventByHost(eventId, userId);
        List<ParticipationRequestDto> result = participationRepository.findAllByEvent(eventId).stream()
                .map(ParticipationMapper::mapParticipationRequestDtoFromParticipationRequest)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    @Transactional
    public ParticipationRequestDto confirmParticipation(long userId, long eventId, long reqId) {
        Event event = checkEventByHost(eventId, userId);
        ParticipationRequest participationRequest = checkEventAndParticipationEquals(reqId, event);

        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == getConfirmedRequests(List.of(eventId))) {
            participationRequest.setStatus(Status.REJECTED);
            throw new InvalidParticipationException("?????????????????? ???????? ???? ????????????????. ???????????? ?????????????????????????? ??????????????????.");
        } else {
            participationRequest.setStatus(Status.CONFIRMED);
            return ParticipationMapper.mapParticipationRequestDtoFromParticipationRequest(participationRequest);
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectParticipation(long userId, long eventId, long reqId) {
        Event event = checkEventByHost(eventId, userId);
        ParticipationRequest participationRequest = checkEventAndParticipationEquals(reqId, event);
        participationRequest.setStatus(Status.REJECTED);
        return ParticipationMapper.mapParticipationRequestDtoFromParticipationRequest(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> getParticipationByUserId(long userId) {
        checkUserExists(userId);
        List<ParticipationRequestDto> participationList = participationRepository.findAllByRequester(userId).stream()
                .map(ParticipationMapper::mapParticipationRequestDtoFromParticipationRequest)
                .collect(Collectors.toList());
        return participationList;
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipation(long userId, long eventId) {
        User user = checkUserExists(userId);
        Event event = checkEvent(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new InvalidParticipationException("???????????? ?????????????? ???? ????????????????????????");
        }
        if (event.getInitiator().getId() == userId) {
            throw new InvalidParticipationException("?????????????????????? ???? ??????????" +
                    " ???????????????? ???????????? ???? ??????????????");
        }
        if (participationRepository.findByEventAndRequester(eventId, userId).isPresent()) {
            throw new ParticipationAlreadyExistsException("???????????? ?????? ?????? ????????????.");
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(userId);
        participationRequest.setEvent(eventId);
        participationRequest.setCreated(LocalDateTime.now());

        int confirmedRequests = getConfirmedRequests(List.of(eventId));

        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == confirmedRequests) {
            participationRequest.setStatus(Status.REJECTED);
            throw new InvalidParticipationException("?????????????????? ???????? ???? ????????????????. ???????????? ?????????????????????????? ??????????????????.");
        }
        if (!event.isRequestModeration() && (event.getParticipantLimit() == 0 ||
                event.getParticipantLimit() > confirmedRequests)) {
            participationRequest.setStatus(Status.CONFIRMED);
        } else {
            participationRequest.setStatus(Status.PENDING);
        }

        ParticipationRequest savedParticipation = participationRepository.save(participationRequest);
        return ParticipationMapper.mapParticipationRequestDtoFromParticipationRequest(savedParticipation);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        ParticipationRequest participationRequest = participationRepository.findByRequesterAndId(userId, requestId).orElseThrow(() ->
                new ParticipationNotFoundException("???????????? ?? id: " + requestId + " ???? ????????????"));
        participationRequest.setStatus(Status.CANCELED);
        return ParticipationMapper.mapParticipationRequestDtoFromParticipationRequest(participationRequest);
    }

    private User checkUserExists(long userId) {
        User user = usersRepository.findById(userId).orElseThrow(() ->
                new UserNotFound("???????????????????????? ?? id: " + userId + " ???? ????????????"));
        return user;
    }

    private Event checkEventByHost(long eventId, long userId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new EventNotFoundException("?????????? ?? id: " + eventId + " ???????????????????????? ?? id: " + userId + " ???? ????????????"));
        return event;
    }

    private Event checkEvent(long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("?????????? ?? id: " + eventId + " ???? ????????????"));
        return event;
    }

    private int getConfirmedRequests(List<Long> ids) {
        int confirmedRequests = participationRepository.findAllByEventInAndStatus(ids, Status.CONFIRMED).size();
        return confirmedRequests;
    }

    private ParticipationRequest checkEventAndParticipationEquals(long reqId, Event event) {
        ParticipationRequest participationRequest = participationRepository.findById(reqId).orElseThrow(() ->
                new ParticipationNotFoundException("???????????? ?? id: " + reqId + " ???? ??????????????"));

        if (!Objects.equals(participationRequest.getEvent(), event.getId())) {
            throw new InvalidParticipationException("???????????? ???????????? ???? ?????????????????? ?? ?????????? ????????????.");
        }

        return participationRequest;
    }
}
