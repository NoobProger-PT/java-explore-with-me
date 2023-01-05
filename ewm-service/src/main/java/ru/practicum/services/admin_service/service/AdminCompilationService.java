package ru.practicum.services.admin_service.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;

public interface AdminCompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    String deleteCompilationById(long complId);

    String deleteEventFromCompilation(long eventId, long complId);

    CompilationDto addEventIntoCompilation(long complId, long eventId);

    CompilationDto removeCompilationFromMainPage(long complId);

    CompilationDto addCompilationOnMainPage(long complId);
}
