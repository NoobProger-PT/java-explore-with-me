package ru.practicum.explore.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class NewCompilationDto {

    private Set<Long> events;
    private boolean pinned = false;
    @NotNull
    private String title;
}
