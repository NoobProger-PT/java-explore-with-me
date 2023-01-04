package ru.practicum.explore.compilation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompilationDto {
    private List<EventShortDto> events;

    private long id;

    private boolean pinned;

    private String title;
}
