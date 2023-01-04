package ru.practicum.services.public_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.client.EventClient;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.State;
import ru.practicum.event.dto.EndPointHitDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.exception.category_exception.CategoryNotFoundException;
import ru.practicum.exception.compilation.CompilationNutFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.services.admin_service.repository.AdminCategoryRepository;
import ru.practicum.services.admin_service.repository.AdminCompilationRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {

    private final PrivateEventsRepository eventsRepository;
    private final AdminCategoryRepository categoryRepository;
    private final AdminCompilationRepository compilationRepository;
    private final EventClient eventClient;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventFullDto> getEvents(String text, List<Long> categories, boolean paid,
                                        String rangeStart, String rangeEnd, boolean onlyAvailable,
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
            startDate = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        if (rangeEnd == null) {
            endDate = LocalDateTime.MAX;
        } else {
            endDate = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }

        List<Category> categoryList = categoryRepository.findAllById(categories);

        List<Event> events = eventsRepository.findAllByAnnotationOrDescriptionContainingIgnoreCaseAndCategoryInAndState(
                text, text, categoryList, State.PUBLISHED, pageRequest);

        if (events.size() == 0) {
            return List.of();
        }

        if (onlyAvailable) {
            sortedEventList = events.stream()
                    .filter(e -> e.getPaid() == paid
                            && e.getConfirmedRequests() < e.getParticipantLimit()
                            && e.getEventDate().isAfter(startDate)
                            && e.getEventDate().isBefore(endDate))
                    .collect(Collectors.toList());
        } else {
            sortedEventList = events.stream()
                    .filter(e -> e.getPaid() == paid
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

        for (Event event : sortedEventList) {
            event.setViews(event.getViews() + 1);
        }

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
        event.setViews(event.getViews() + 1);
        eventClient.add(endPointHitDto);
        return EventMapper.mapToEventFullDtoFromEvent(event);
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        List<CompilationDto> result;
        if (pinned) {
            result = compilationRepository.findAllByPinned(true,
                            PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                    .map(CompilationMapper::mapToCompilationDtoFromCompilation)
                    .collect(Collectors.toList());
        } else {
            result = compilationRepository.findAll(
                    PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                    .map(CompilationMapper::mapToCompilationDtoFromCompilation)
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public CompilationDto getCompilationById(long complId) {
        Compilation compilation = compilationRepository.findById(complId).orElseThrow(
                () -> new CompilationNutFoundException("Такой подборки не существует"));
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilation);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        List<CategoryDto> result = categoryRepository.findAll(
                PageRequest.of(from, size, Sort.by("id").ascending())).stream()
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public CategoryDto getCategory(long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(
                () -> new CategoryNotFoundException("категория не найдена"));
        return CategoryMapper.mapToCategoryDto(category);
    }
}
