package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService service;

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                           LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                           LocalDateTime end,
                                       @RequestParam List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на получение статистики");
        return service.getStats(start, end, uris, unique);
    }

    @GetMapping("/hits")
    public String getHits(@RequestParam List<String> uris) {
        log.info("Запрос на получение просмотров");
        return service.getHits(uris);
    }

    @PostMapping("/hit")
    public EndPointHitDto addHit(@RequestBody EndPointHitDto endPointHitDto) {
        log.info("Запрос на добавление данных");
        return service.addHit(endPointHitDto);
    }
}
