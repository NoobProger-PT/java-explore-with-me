package ru.practicum.services.admin_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.services.admin_service.service.AdminCompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
@Validated
public class AdminCompilationController {

    private final AdminCompilationService service;

    @PostMapping("/compilations")
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Получен запрос на добавление новой подборки");
        return service.addCompilation(newCompilationDto);
    }

    @PatchMapping("/compilations/{complId}/events/{eventId}")
    public CompilationDto addEventIntoCompilation(@PathVariable @Positive long complId,
                                                  @PathVariable @Positive long eventId) {
        log.info("Добавление события в подборку");
        return service.addEventIntoCompilation(complId, eventId);
    }

    @PatchMapping("/compilations/{complId}/pin")
    public CompilationDto addCompilationOnMainPage(@PathVariable @Positive long complId) {
        log.info("Закрепления подборки на главной странице");
        return service.addCompilationOnMainPage(complId);
    }

    @DeleteMapping("/compilations/{complId}")
    public String deleteCompilation(@PathVariable @Positive long complId) {
        log.info("Удаление подборки");
        return service.deleteCompilationById(complId);
    }

    @DeleteMapping("/compilations/{complId}/events/{eventId}")
    public String deleteEventFromCompilation(@PathVariable @Positive long eventId,
                                             @PathVariable @Positive long complId) {
        log.info("Удаление события из подборки");
        return service.deleteEventFromCompilation(complId, eventId);
    }

    @DeleteMapping("/compilations/{complId}/pin")
    public CompilationDto removeCompilationFromMainPage(@PathVariable @Positive long complId) {
        log.info("Убрать подборку из главной страницы");
        return service.removeCompilationFromMainPage(complId);
    }
}
