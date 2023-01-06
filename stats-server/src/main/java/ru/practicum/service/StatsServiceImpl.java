package ru.practicum.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndPointHitMapper;
import ru.practicum.model.EndPointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        LocalDateTime startTime;
        LocalDateTime endTime;
        List<ViewStatsDto> result = new ArrayList<>();
        Set<String> uniqIp = new HashSet<>();

        if (start != null) {
            startTime = start;
        } else {
            startTime = LocalDateTime.now();
        }
        if (end != null) {
            endTime = end;
        } else {
            endTime = LocalDateTime.MAX;
        }

        List<EndPointHit> endPointHits = repository.findAllByTimeStampBetween(startTime, endTime);

        if (endPointHits.size() == 0) {
            List<ViewStatsDto> emptyList = new ArrayList<>();
            emptyList.add(new ViewStatsDto());
            return emptyList;
        }

        if (unique) {
            for (String uri : uris) {
                List<EndPointHit> sortHits = endPointHits.stream()
                        .filter(h -> h.getUri().contains(uri))
                        .map(h -> {
                            if (!uniqIp.contains(h.getIp())) {
                                uniqIp.add(h.getIp());
                            }
                            return h;
                        })
                        .collect(Collectors.toList());
                long hits = uniqIp.size();
                ViewStatsDto viewStatsDto = new ViewStatsDto();
                viewStatsDto.setHits(hits);
                viewStatsDto.setUri(uri);
                viewStatsDto.setApp(sortHits.get(0).getApp());
                result.add(viewStatsDto);
                uniqIp.clear();
            }
        } else {
            for (String uri : uris) {
                List<EndPointHit> sortHits = endPointHits.stream()
                        .filter(h -> h.getUri().contains(uri))
                        .collect(Collectors.toList());
                long hits = sortHits.size();
                ViewStatsDto viewStatsDto = new ViewStatsDto();
                viewStatsDto.setHits(hits);
                viewStatsDto.setUri(uri);
                viewStatsDto.setApp(sortHits.get(0).getApp());
                result.add(viewStatsDto);
            }
        }
        return result;
    }

    @Override
    public EndPointHitDto addHit(EndPointHitDto endPointHitDto) {
        EndPointHit endPointHit = repository.save(EndPointHitMapper.mapToEndPointHitFromEndPointHitDto(endPointHitDto));
        return EndPointHitMapper.mapToEndPointHitDtoFromEndPointHit(endPointHit);
    }
}