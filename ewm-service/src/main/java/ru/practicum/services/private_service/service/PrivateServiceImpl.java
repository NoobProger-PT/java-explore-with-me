package ru.practicum.services.private_service.service;

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
import ru.practicum.event.location.dto.LocationDto;
import ru.practicum.event.location.mapper.LocationMapper;
import ru.practicum.event.location.model.Location;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.event_exception.PublishedEventException;
import ru.practicum.exception.participation_exception.InvalidParticipationException;
import ru.practicum.exception.participation_exception.ParticipationAlreadyExistsException;
import ru.practicum.exception.participation_exception.ParticipationNotFoundException;
import ru.practicum.exception.user_exception.InvalidUserException;
import ru.practicum.exception.user_exception.UserNotFound;
import ru.practicum.participation.Status;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.mapper.ParticipationMapper;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.repository.AdminUsersRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.repository.PrivateLocationRepository;
import ru.practicum.services.private_service.repository.PrivateParticipationRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrivateServiceImpl implements PrivateService {

    private final PrivateEventsRepository eventsRepository;
    private final AdminUsersRepository usersRepository;
    private final PrivateLocationRepository locationRepository;
    private final AdminCategoryRepository categoryRepository;
    private final PrivateParticipationRepository participationRepository;

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto newEventDto, long userId) {
        Location location = checkLocation(newEventDto.getLocation());
        User user = checkUserExists(userId);
        Event event = EventMapper.mapToEventFromNewEventDto(newEventDto);
        event.setCategory(checkCategory(newEventDto.getCategory()));
        event.setLocation(location);
        event.setUser(user);
        event.setState(State.PENDING);
        Event savedEvent = eventsRepository.save(event);
        return EventMapper.mapToEventFullDtoFromEvent(savedEvent);
    }

    @Override
    public List<EventShortDto> getByUserId(long userId, int from, int size) {
        checkUserExists(userId);
        List<EventShortDto> events = eventsRepository.findAllByUserId(userId,
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
        if (userId != event.getUser().getId()) {
            throw new InvalidUserException("Данный пользователь не является автором ивента.");
        }
        if (updateEventDto.getAnnotation() != null && !updateEventDto.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null && !updateEventDto.getCategory().isBlank()) {
            event.setCategory(checkCategory(updateEventDto.getCategory()));
        }
        if (updateEventDto.getDescription() != null && !updateEventDto.getDescription().isBlank()) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getPaid() != event.getPaid()) {
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
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
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
        if (event.getUser().getId() == userId) {
            throw new InvalidParticipationException("Организатор не может" +
                    " подавать заявку на участие");
        }
        if (participationRepository.findByEventAndRequester(eventId, userId).isPresent()) {
            throw new ParticipationAlreadyExistsException("Запрос уже был создан.");
        }
        if (event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new InvalidParticipationException("Свободных мест не осталось");
        }
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(userId);
        participationRequest.setEvent(eventId);
        participationRequest.setCreated(LocalDateTime.now());
        if (!event.getRequestModeration()) {
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
        checkUserExists(userId);
        ParticipationRequest participationRequest = participationRepository.findById(requestId).orElseThrow(() ->
                new ParticipationNotFoundException("Запрос с id: " + requestId + " не найден"));
        participationRequest.setStatus(Status.CANCELED);
        return ParticipationMapper.mapParticipationRequestDtoFromParticipationRequest(participationRequest);
    }

    private Location createLocationInDB(LocationDto locationDto) {
        Location location = LocationMapper.mapToLocation(locationDto);
        return locationRepository.save(location);
    }

    private Location checkLocation(LocationDto locationDto) {
        Optional<Location> location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        if (location.isEmpty()) {
            return createLocationInDB(locationDto);
        } else {
            return location.get();
        }
    }

    private Category checkCategory(String categoryId) {
        Category category = categoryRepository.findById(Long.parseLong(categoryId)).orElseThrow(() ->
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
        Event event = eventsRepository.findByIdAndUserId(eventId, userId).orElseThrow(() ->
                new EventNotFoundException("Ивент с id: " + eventId + " пользователя с id: " + userId + " не найден"));
        return event;
    }

    private Event checkEvent(long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Ивент с id: " + eventId + " не найден"));
        return event;
    }
}
