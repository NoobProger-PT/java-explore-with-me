package ru.practicum.explore.controller;

import ru.practicum.explore.dto.EndPointHitDto;
import ru.practicum.explore.dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {

    private final StatsService service;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam String start,
                                       @RequestParam String end,
                                       @RequestParam(defaultValue = "List.of()") List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики");
        return service.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    public EndPointHitDto addHit(@RequestBody EndPointHitDto endPointHitDto) {
        log.info("Запрос на добавление данных");
        return service.addHit(endPointHitDto);
    }
}
