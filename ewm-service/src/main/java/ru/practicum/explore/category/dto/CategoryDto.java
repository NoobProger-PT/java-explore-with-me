package ru.practicum.explore.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.Marker;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
public class CategoryDto {
    @NotNull(groups = {Marker.Update.class})
    private Long id;
    @NotNull(groups = {Marker.Update.class, Marker.Create.class})
    private String name;
}
