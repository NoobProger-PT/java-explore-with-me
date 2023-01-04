package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.EndPointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndPointHit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndPointHitMapper {
    public static ViewStatsDto mapToViewDtoFromHit(EndPointHit endPointHit) {
        ViewStatsDto viewStatsDto = new ViewStatsDto();
        viewStatsDto.setApp(endPointHit.getApp());
        viewStatsDto.setUri(endPointHit.getUri());
        return viewStatsDto;
    }

    public static EndPointHitDto mapToEndPointHitDtoFromEndPointHit(EndPointHit endPointHit) {
        EndPointHitDto endPointHitDto = new EndPointHitDto();
        endPointHitDto.setId(endPointHit.getId());
        endPointHitDto.setIp(endPointHit.getIp());
        endPointHitDto.setApp(endPointHit.getApp());
        endPointHitDto.setUri(endPointHit.getUri());
        endPointHitDto.setTimeStamp(endPointHit.getTimeStamp());
        return endPointHitDto;
    }

    public static EndPointHit mapToEndPointHitFromEndPointHitDto(EndPointHitDto endPointHitDto) {
        EndPointHit endPointHit = new EndPointHit();
        endPointHit.setId(endPointHitDto.getId());
        endPointHit.setApp(endPointHitDto.getApp());
        endPointHit.setUri(endPointHitDto.getUri());
        endPointHit.setTimeStamp(endPointHitDto.getTimeStamp());
        return endPointHit;
    }
}
