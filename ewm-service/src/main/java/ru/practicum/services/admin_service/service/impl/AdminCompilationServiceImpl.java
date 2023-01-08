package ru.practicum.services.admin_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.exception.compilation.CompilationNutFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.services.admin_service.repository.AdminCompilationRepository;
import ru.practicum.services.admin_service.service.AdminCompilationService;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final AdminCompilationRepository compilationRepository;

    private final PrivateEventsRepository eventsRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            newCompilationDto.setEvents(Set.of());
        }
        List<Event> eventList = eventsRepository.findAllById(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.mapToCompilationFromNewCompilationDto(newCompilationDto);
        compilation.setEvents(eventList);
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public String deleteCompilationById(long complId) {
        Compilation compilation = checkCompilation(complId);
        compilationRepository.deleteById(complId);
        return "Подборка удалена";
    }

    @Override
    @Transactional
    public String deleteEventFromCompilation(long complId, long eventId) {
        Compilation compilation = checkCompilation(complId);
        compilation.getEvents().removeIf(c -> Objects.equals(c.getId(), eventId));
        return "ивент удален из подборки";
    }

    @Override
    @Transactional
    public CompilationDto addEventIntoCompilation(long complId, long eventId) {
        Compilation compilation = checkCompilation(complId);
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("ивент не найден"));
        compilation.getEvents().add(event);
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilation);
    }

    @Override
    @Transactional
    public CompilationDto removeCompilationFromMainPage(long complId) {
        Compilation compilation = checkCompilation(complId);
        compilation.setPinned(false);
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilation);
    }

    @Override
    @Transactional
    public CompilationDto addCompilationOnMainPage(long complId) {
        Compilation compilation = checkCompilation(complId);
        compilation.setPinned(true);
        return CompilationMapper.mapToCompilationDtoFromCompilation(compilation);
    }

    private Compilation checkCompilation(long complId) {
        Compilation compilation = compilationRepository.findById(complId)
                .orElseThrow(() -> new CompilationNutFoundException("Подборка не найдена"));
        return compilation;
    }
}
