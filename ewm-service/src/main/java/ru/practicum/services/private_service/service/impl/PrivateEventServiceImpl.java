package ru.practicum.services.private_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.event.State;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventDto;
import ru.practicum.event.location.mapper.LocationMapper;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.event_exception.PublishedEventException;
import ru.practicum.exception.user_exception.InvalidUserException;
import ru.practicum.exception.user_exception.UserNotFound;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.repository.AdminUsersRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.service.PrivateEventService;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final PrivateEventsRepository eventsRepository;
    private final AdminCategoryRepository categoryRepository;
    private final AdminUsersRepository usersRepository;

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
        List<EventShortDto> events = eventsRepository.findAllByInitiatorId(userId,
                        PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
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
        if (updateEventDto.getPaid() != event.isPaid()) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != event.getParticipantLimit()) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getTitle() != null && !updateEventDto.getTitle().isBlank()) {
            event.setTitle(updateEventDto.getTitle());
        }
        event.setState(State.PENDING);
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    @Override
    public EventFullDto getByUserIdAndEventId(long userId, long eventId) {
        Event event = checkEventByHost(eventId, userId);
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(long userId, long eventId) {
        Event event = checkEventByHost(eventId, userId);
        event.setState(State.CANCELED);
        return EventMapper.mapToEventFullDtoFromEvent(event);
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
}
