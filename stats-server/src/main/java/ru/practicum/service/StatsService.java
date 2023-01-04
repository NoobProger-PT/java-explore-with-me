package ru.practicum.service;


import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {

    List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique);

    EndPointHitDto addHit(EndPointHitDto endPointHitDto);
}
