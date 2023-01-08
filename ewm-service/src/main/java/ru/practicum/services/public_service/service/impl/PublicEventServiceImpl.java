package ru.practicum.services.public_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.client.EventClient;
import ru.practicum.event.State;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.ViewStatsDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.participation.Status;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.repository.PrivateParticipationRepository;
import ru.practicum.services.public_service.service.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventClient eventClient;
    private final PrivateEventsRepository eventsRepository;
    private final AdminCategoryRepository categoryRepository;
    private final PrivateParticipationRepository participationRepository;

    @Override
    public List<EventFullDto> getEvents(String text, List<Long> categories, boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                        String sort, int from, int size, HttpServletRequest request) {
        PageRequest pageRequest;
        LocalDateTime startDate;
        LocalDateTime endDate;
        List<Long> ids = new ArrayList<>();
        List<Category> categoryList;
        List<Event> events;

        if (text == null) {
            text = "";
        }

        if (sort != null && sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from, size, Sort.by("eventDate").ascending());
        } else if (sort != null && sort.equals("VIEWS")) {
            pageRequest = PageRequest.of(from, size, Sort.by("views").ascending());
        } else {
            pageRequest = PageRequest.of(from, size, Sort.by("Id").ascending());
        }

        if (categories.size() != 0) {
            categoryList = categoryRepository.findAllById(categories);
        } else {
            categoryList = categoryRepository.findAll();
        }

        if (rangeStart == null) {
            startDate = LocalDateTime.of(1000,10, 10, 10, 10);
        } else {
            startDate = rangeStart;
        }
        if (rangeEnd == null) {
            endDate = LocalDateTime.of(5000, 10, 10, 10, 10);;
        } else {
            endDate = rangeEnd;
        }

         events = eventsRepository.findAllByAnnotationOrDescriptionContainingIgnoreCaseAndCategoryInAndStateAndPaidAndEventDateBetween(
                text, text, categoryList, State.PUBLISHED, paid, startDate, endDate, pageRequest);

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

        Map<Long, Long> requires = participationRepository.findAllByEventInAndStatus(ids, Status.CONFIRMED).stream()
                .collect(groupingBy(ParticipationRequest::getEvent, counting()));

        if (onlyAvailable) {
            result.removeIf(e -> e.getParticipantLimit() < requires.get(e.getId()));
        }

        eventClient.add(request);

        if (ids.isEmpty()) {
            return result;
        }

        Map<String, Long> mapHits = eventClient.getViews(ids).stream().collect(groupingBy(ViewStatsDto::getUri, counting()));
        if (!mapHits.isEmpty()) {
            for (EventFullDto event : result) {
                event.setViews(Math.toIntExact(mapHits.get("/events/" + event.getId())));
            }
        }

        if (!requires.isEmpty()) {
            for (EventFullDto event : result) {
                event.setConfirmedRequests(requires.get(event.getId()).intValue());
            }
        }
        return result;
    }

    @Override
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Event event = eventsRepository.findById(id).orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        eventClient.add(request);
        return setParams(event);
    }

    private int getViews(List<Long> ids) {
        List<ViewStatsDto> list = eventClient.getViews(ids);
        if (list.isEmpty()) {
            return -1;
        }
        int views = list.size();
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
