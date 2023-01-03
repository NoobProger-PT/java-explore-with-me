package ru.practicum.explore.user.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}