package ru.practicum.services.public_service.service;

import ru.practicum.event.dto.EventFullDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventFullDto> getEvents(String text, List<Long> categories, boolean paid, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size,
                                 HttpServletRequest request);

    EventFullDto getEventById(long id, HttpServletRequest request);
}
