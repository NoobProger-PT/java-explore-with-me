package ru.practicum.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class NewCompilationDto {

    private Set<Long> events;
    private boolean pinned;
    @NotBlank
    private String title;
}
