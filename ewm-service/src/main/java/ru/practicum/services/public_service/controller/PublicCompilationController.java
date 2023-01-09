package ru.practicum.services.public_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.services.public_service.service.PublicCompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

    private final PublicCompilationService service;

    @GetMapping
    public List<CompilationDto> get(@RequestParam(required = false) boolean pinned,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Получение подборки событий");
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/{complId}")
    public CompilationDto getById(@PathVariable @Positive long complId) {
        log.info("Получение подборки событий по Id");
        return service.getCompilationById(complId);
    }
}
