package ru.practicum.services.private_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.client.EventClient;
import ru.practicum.event.State;
import ru.practicum.event.dto.*;
import ru.practicum.event.location.mapper.LocationMapper;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.event_exception.PublishedEventException;
import ru.practicum.exception.user_exception.InvalidUserException;
import ru.practicum.exception.user_exception.UserNotFound;
import ru.practicum.participation.Status;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.repository.AdminUsersRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.repository.PrivateParticipationRepository;
import ru.practicum.services.private_service.service.PrivateEventService;
import ru.practicum.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final PrivateEventsRepository eventsRepository;
    private final AdminCategoryRepository categoryRepository;
    private final AdminUsersRepository usersRepository;
    private final PrivateParticipationRepository participationRepository;
    private final EventClient eventClient;



    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto newEventDto, long userId) {
        User user = checkUserExists(userId);
        Event event = EventMapper.mapToEventFromNewEventDto(newEventDto);
        event.setCategory(checkCategory(newEventDto.getCategory()));
        event.setLocation(LocationMapper.mapToLocation(newEventDto.getLocation()));
        event.setInitiator(user);
        event.setState(State.PENDING);
        Event savedEvent = eventsRepository.save(event);
        return EventMapper.mapToEventFullDtoFromEvent(savedEvent);
    }

    @Override
    public List<EventShortDto> getByUserId(long userId, int from, int size) {
        checkUserExists(userId);
        List<Long> ids = new ArrayList<>();
        List<EventShortDto> events = eventsRepository.findAllByInitiatorId(userId,
                        PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                .map(EventMapper::mapToEventShortDto)
                .map(e -> {
                    ids.add(e.getId());
                    return e;
                })
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return events;
        }

        Map<String, List<ViewStatsDto>> mapHits = eventClient.getViews(ids).stream().collect(groupingBy(ViewStatsDto::getUri));
        if (!mapHits.isEmpty()) {
            for (EventShortDto event : events) {
                event.setViews((int) mapHits.get("/events/" + event.getId()).get(0).getHits());
            }
        }
        Map<Long, List<ParticipationRequest>> mapReq = participationRepository.findAllByEventInAndStatus(ids, Status.CONFIRMED).stream()
                .collect(groupingBy(ParticipationRequest::getEvent));
        if (!mapReq.isEmpty()) {
            for (EventShortDto event : events) {
                event.setConfirmedRequests(mapReq.get(event.getId()).size());
            }
        }
        return events;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(UpdateEventDto updateEventDto, long userId) {
        checkUserExists(userId);
        Event event = checkState(updateEventDto.getEventId());
        if (userId != event.getInitiator().getId()) {
            throw new InvalidUserException("Данный пользователь не является автором ивента.");
        }
        if (updateEventDto.getAnnotation() != null && !updateEventDto.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            event.setCategory(checkCategory(updateEventDto.getCategory()));
        }
        if (updateEventDto.getDescription() != null && !updateEventDto.getDescription().isBlank()) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getTitle() != null && !updateEventDto.getTitle().isBlank()) {
            event.setTitle(updateEventDto.getTitle());
        }
        event.setState(State.PENDING);
        return setParams(event);
    }

    @Override
    public EventFullDto getByUserIdAndEventId(long userId, long eventId) {
        Event event = checkEventByHost(eventId, userId);
        return setParams(event);
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(long userId, long eventId) {
        Event event = checkEventByHost(eventId, userId);
        event.setState(State.CANCELED);
        return setParams(event);
    }

    private Category checkCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new CategoryNotFoundException("Категория с id: " + categoryId + " не найдена"));
        return category;
    }

    private Event checkState(long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Ивент с id: " + eventId + " не найден"));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new PublishedEventException("Опубликованный ивент нельзя редактировать.");
        } else {
            return event;
        }
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

    private int getViews(List<Long> ids) {
        List<ViewStatsDto> list = eventClient.getViews(ids);
        if (list.isEmpty()) {
            return -1;
        }
        int views = (int) list.get(0).getHits();
        return views;
    }

    private int getConfirmedRequests(List<Long> ids) {
        int confirmedRequests = participationRepository.findAllByEventInAndStatus(ids, Status.CONFIRMED).size();
        return confirmedRequests;
    }

    private EventFullDto setParams(Event event) {
        EventFullDto eventFullDto = EventMapper.mapToEventFullDtoFromEvent(event);
        int views = getViews(List.of(eventFullDto.getId()));
        eventFullDto.setViews(views);
        int confirmedRequests = getConfirmedRequests(List.of(eventFullDto.getId()));
        eventFullDto.setConfirmedRequests(confirmedRequests);
        return eventFullDto;
    }
}
