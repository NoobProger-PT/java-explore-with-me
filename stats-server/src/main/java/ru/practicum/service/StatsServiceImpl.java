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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {

        LocalDateTime startTime;
        LocalDateTime endTime;
        Set<String> ips = new HashSet<>();
        List<EndPointHit> uniqIp = new ArrayList<>();
        List<ViewStatsDto> result = new ArrayList<>();

        if (start != null) {
            startTime = LocalDateTime.parse(start, dateTimeFormatter);
        } else {
            startTime = LocalDateTime.now();
        }
        if (end != null) {
            endTime = LocalDateTime.parse(end, dateTimeFormatter);
        } else {
            endTime = LocalDateTime.MAX;
        }

        if (unique) {
            for (String uri : uris) {
                List<EndPointHit> endPointHits = repository
                        .findAllByUriContainingIgnoreCaseAndTimeStampAfterAndTimeStampBefore(uri, startTime, endTime);
                if (endPointHits.size() == 0) {
                    return List.of();
                }
                for (EndPointHit e : endPointHits) {
                    if (!ips.contains(e.getIp())) {
                        ips.add(e.getIp());
                        uniqIp.add(e);
                    }
                }
                ViewStatsDto viewStatsDto = new ViewStatsDto();
                viewStatsDto.setHits(uniqIp.size());
                viewStatsDto.setUri(uri);
                viewStatsDto.setApp(endPointHits.get(0).getApp());
                result.add(viewStatsDto);
            }
        } else {
            for (String uri : uris) {
                List<EndPointHit> endPointHits = repository
                        .findAllByUriContainingIgnoreCaseAndTimeStampAfterAndTimeStampBefore(uri, startTime, endTime);
                if (endPointHits.size() == 0) {
                    return List.of();
                }
                ViewStatsDto viewStatsDto = new ViewStatsDto();
                viewStatsDto.setHits(endPointHits.size());
                viewStatsDto.setUri(uri);
                viewStatsDto.setApp(endPointHits.get(0).getApp());
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