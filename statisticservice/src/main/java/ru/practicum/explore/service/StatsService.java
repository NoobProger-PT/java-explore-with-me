package ru.practicum.explore.service;

import ru.practicum.explore.dto.EndPointHitDto;
import ru.practicum.explore.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {

    List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique);

    EndPointHitDto addHit(EndPointHitDto endPointHitDto);
}
