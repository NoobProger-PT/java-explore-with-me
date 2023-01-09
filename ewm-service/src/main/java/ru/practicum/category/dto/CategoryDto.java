package ru.practicum.category.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
public class CategoryDto {
    @NotNull(groups = {Marker.Update.class})
    private Long id;
    @NotBlank(groups = {Marker.Update.class, Marker.Create.class})
    private String name;
}
