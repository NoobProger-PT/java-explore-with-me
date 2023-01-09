package ru.practicum.services.admin_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.client.EventClient;
import ru.practicum.event.State;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.ViewStatsDto;
import ru.practicum.event.location.mapper.LocationMapper;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.event_exception.PublishedEventException;
import ru.practicum.exception.event_exception.WrongEventDateException;
import ru.practicum.participation.Status;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.service.AdminEventService;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.repository.PrivateParticipationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventClient eventClient;
    private final PrivateEventsRepository eventsRepository;
    private final AdminCategoryRepository categoryRepository;
    private final PrivateParticipationRepository participationRepository;

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, int from, int size) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        List<Long> ids = new ArrayList<>();

        List<State> stateList = states.stream().map(State::valueOf).collect(Collectors.toList());

        if (rangeStart != null) {
            startDate = rangeStart;
        } else {
            startDate = LocalDateTime.of(1000,10, 10, 10, 10);
        }

        if (rangeEnd != null) {
            endDate = rangeEnd;
        } else {
            endDate = LocalDateTime.of(5000, 10, 10, 10, 10);
        }

        List<Event> events = eventsRepository.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(users,
                stateList, categories, startDate, endDate, PageRequest.of(from, size, Sort.by("id")
                .ascending()));

        if (events.size() == 0) {
            return List.of();
        }

        List<EventFullDto> result = events.stream()
                .map(EventMapper::mapToEventFullDtoFromEvent)
                .map(e -> {
                    ids.add(e.getId());
                    return e;
                })
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return result;
        }

        Map<String, List<ViewStatsDto>> mapHits = eventClient.getViews(ids).stream().collect(groupingBy(ViewStatsDto::getUri));
        if (!mapHits.isEmpty()) {
            for (EventFullDto event : result) {
                event.setViews((int) mapHits.get("/events/" + event.getId()).get(0).getHits());
            }
        }
        Map<Long, List<ParticipationRequest>> mapReq = participationRepository.findAllByEventInAndStatus(ids, Status.CONFIRMED).stream()
                .collect(groupingBy(ParticipationRequest::getEvent));
        if (!mapReq.isEmpty()) {
            for (EventFullDto event : result) {
                event.setConfirmedRequests(mapReq.get(event.getId()).size());
            }
        }
        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        event.setAnnotation(adminUpdateEventRequest.getAnnotation());
        Category category = checkCategory(adminUpdateEventRequest.getCategory());
        event.setCategory(category);
        event.setDescription(adminUpdateEventRequest.getDescription());
        event.setEventDate(adminUpdateEventRequest.getEventDate());
        if (adminUpdateEventRequest.getLocation() != null) {
            event.setLocation(LocationMapper.mapToLocation(adminUpdateEventRequest.getLocation()));
        }
        event.setPaid(adminUpdateEventRequest.isPaid());
        event.setParticipantLimit(adminUpdateEventRequest.getParticipantLimit());
        event.setRequestModeration(adminUpdateEventRequest.isRequestModeration());
        event.setTitle(adminUpdateEventRequest.getTitle());
        return setParams(event);
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(long eventId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        if (event.getState() != State.PENDING) {
            throw new PublishedEventException("Можно опубликовывать только события со статусом ожидания.");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new WrongEventDateException("Дата начала ивента должна быть не раньше чем через час от текущего момента.");
        }
        event.setState(State.PUBLISHED);
        return setParams(event);
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(long eventId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        if (event.getState() == State.PUBLISHED) {
            throw new PublishedEventException("Ивент уже опубликован. Отклонить невозможно.");
        }
        event.setState(State.CANCELED);
        return setParams(event);
    }

    private Category checkCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new CategoryNotFoundException("Категория с id: " + categoryId + " не найдена"));
        return category;
    }

    private int getViews(List<Long> ids) {
        List<ViewStatsDto> list = eventClient.getViews(ids);
        if (list.isEmpty()) {
            return 0;
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
