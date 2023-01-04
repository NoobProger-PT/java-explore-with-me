package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    private Integer hits;
}
