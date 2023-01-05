package ru.practicum.services.admin_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.event.State;
import ru.practicum.event.dto.AdminUpdateEventRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.location.mapper.LocationMapper;
import ru.practicum.event.location.model.Location;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.service.AdminEventService;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final PrivateEventsRepository eventsRepository;

    private final AdminCategoryRepository categoryRepository;

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, int from, int size) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        List<Event> events = eventsRepository.findAllByInitiatorIdIn(users, PageRequest.of(from, size, Sort.by("id")
                .ascending())).stream().collect(Collectors.toList());

        if (events.size() == 0) {
            return List.of();
        }

        if (rangeStart != null) {
            startDate = rangeStart;
        } else {
            startDate = LocalDateTime.now();
        }

        if (rangeEnd != null) {
            endDate = rangeEnd;
        } else {
            endDate = LocalDateTime.MAX;
        }

        List<EventFullDto> result = events.stream()
                .filter(e -> states.contains(e.getState().name())
                        && categories.contains(e.getCategory().getId())
                        && e.getEventDate().isAfter(startDate)
                        && e.getEventDate().isBefore(endDate))
                .map(EventMapper::mapToEventFullDtoFromEvent)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long eventId, AdminUpdateEventRequest adminUpdateEventRequest) {
        Location location;
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
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(long eventId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        event.setState(State.PUBLISHED);
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    @Override
    @Transactional
    public EventFullDto rejectEvent(long eventId) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        event.setState(State.CANCELED);
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    private Category checkCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                new CategoryNotFoundException("Категория с id: " + categoryId + " не найдена"));
        return category;
    }
}
