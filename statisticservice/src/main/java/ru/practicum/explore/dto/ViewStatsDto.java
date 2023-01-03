package ru.practicum.explore.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    private Integer hits;
}
