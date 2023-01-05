package ru.practicum.services.public_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.client.EventClient;
import ru.practicum.event.State;
import ru.practicum.event.dto.EndPointHitDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.public_service.service.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventClient eventClient;
    private final PrivateEventsRepository eventsRepository;
    private final AdminCategoryRepository categoryRepository;

    @Override
    public List<EventFullDto> getEvents(String text, List<Long> categories, boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                        String sort, int from, int size, HttpServletRequest request) {
        PageRequest pageRequest;
        LocalDateTime startDate;
        LocalDateTime endDate;
        List<Event> sortedEventList;
        if (sort != null && sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from, size, Sort.by("eventDate").ascending());
        } else if (sort != null && sort.equals("VIEWS")) {
            pageRequest = PageRequest.of(from, size, Sort.by("views").ascending());
        } else {
            pageRequest = PageRequest.of(from, size, Sort.by("Id").ascending());
        }

        if (rangeStart == null) {
            startDate = LocalDateTime.now();
        } else {
            startDate = rangeStart;
        }
        if (rangeEnd == null) {
            endDate = LocalDateTime.MAX;
        } else {
            endDate = rangeEnd;
        }

        List<Category> categoryList = categoryRepository.findAllById(categories);

        List<Event> events = eventsRepository.findAllByAnnotationOrDescriptionContainingIgnoreCaseAndCategoryInAndState(
                text, text, categoryList, State.PUBLISHED, pageRequest);

        if (events.size() == 0) {
            return List.of();
        }

        if (onlyAvailable) {
            sortedEventList = events.stream()
                    .filter(e -> e.isPaid() == paid
                            //&& e.getConfirmedRequests() < e.getParticipantLimit()
                            && e.getEventDate().isAfter(startDate)
                            && e.getEventDate().isBefore(endDate))
                    .collect(Collectors.toList());
        } else {
            sortedEventList = events.stream()
                    .filter(e -> e.isPaid() == paid
                            && e.getEventDate().isAfter(startDate)
                            && e.getEventDate().isBefore(endDate))
                    .collect(Collectors.toList());
        }

        List<EventFullDto> result = sortedEventList.stream()
                .map(EventMapper::mapToEventFullDtoFromEvent)
                .collect(Collectors.toList());

        EndPointHitDto endPointHitDto = new EndPointHitDto();
        endPointHitDto.setApp("mainService");
        endPointHitDto.setIp(request.getRemoteAddr());
        endPointHitDto.setUri(request.getRequestURI());

//        for (Event event : sortedEventList) {
//            event.setViews(event.getViews() + 1);
//        }

        eventClient.add(endPointHitDto);

        return result;
    }

    @Override
    public EventFullDto getEventById(long id, HttpServletRequest request) {
        Event event = eventsRepository.findById(id).orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        EndPointHitDto endPointHitDto = new EndPointHitDto();
        endPointHitDto.setApp("mainService");
        endPointHitDto.setIp(request.getRemoteAddr());
        endPointHitDto.setUri(request.getRequestURI());
        //event.setViews(event.getViews() + 1);
        eventClient.add(endPointHitDto);
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }
}
