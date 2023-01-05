package ru.practicum.services.private_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.event.State;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.event_exception.PublishedEventException;
import ru.practicum.exception.participation_exception.InvalidParticipationException;
import ru.practicum.exception.participation_exception.ParticipationAlreadyExistsException;
import ru.practicum.exception.participation_exception.ParticipationNotFoundException;
import ru.practicum.exception.user_exception.UserNotFound;
import ru.practicum.participation.Status;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.mapper.ParticipationMapper;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.repository.AdminUsersRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.repository.PrivateParticipationRepository;
import ru.practicum.services.private_service.service.PrivateParticipationService;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
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
        ParticipationRequest participationRequest = participationRepository.findById(reqId).orElseThrow(() ->
                new ParticipationNotFoundException("Заявка с id: " + reqId + " не найдена"));
        participationRequest.setStatus(Status.CONFIRMED);
//        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        return ParticipationMapper.mapParticipationRequestDtoFromParticipationRequest(participationRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto rejectParticipation(long userId, long eventId, long reqId) {
        checkEventByHost(eventId, userId);
        ParticipationRequest participationRequest = participationRepository.findById(reqId).orElseThrow(() ->
                new ParticipationNotFoundException("Заявка с id: " + reqId + " не найдена"));
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
            throw new InvalidParticipationException("Данное событие не опубликовано");
        }
        if (event.getInitiator().getId() == userId) {
            throw new InvalidParticipationException("Организатор не может" +
                    " подавать заявку на участие");
        }
        if (participationRepository.findByEventAndRequester(eventId, userId).isPresent()) {
            throw new ParticipationAlreadyExistsException("Запрос уже был создан.");
        }
//        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
//            throw new InvalidParticipationException("Свободных мест не осталось");
//        }
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(userId);
        participationRequest.setEvent(eventId);
        participationRequest.setCreated(LocalDateTime.now());
//        if (!event.getRequestModeration()) {
//            participationRequest.setStatus(Status.CONFIRMED);
//        } else {
        participationRequest.setStatus(Status.PENDING);
//        }

        ParticipationRequest savedParticipation = participationRepository.save(participationRequest);
        return ParticipationMapper.mapParticipationRequestDtoFromParticipationRequest(savedParticipation);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        checkUserExists(userId);
        ParticipationRequest participationRequest = participationRepository.findById(requestId).orElseThrow(() ->
                new ParticipationNotFoundException("Запрос с id: " + requestId + " не найден"));
        participationRequest.setStatus(Status.CANCELED);
        return ParticipationMapper.mapParticipationRequestDtoFromParticipationRequest(participationRequest);
    }

    private User checkUserExists(long userId) {
        User user = usersRepository.findById(userId).orElseThrow(() ->
                new UserNotFound("Пользователь с id: " + userId + " не найден"));
        return user;
    }

    private Event checkEventByHost(long eventId, long userId) {
        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new EventNotFoundException("Ивент с id: " + eventId + " пользователя с id: " + userId + " не найден"));
        return event;
    }

    private Event checkEvent(long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Ивент с id: " + eventId + " не найден"));
        return event;
    }
}