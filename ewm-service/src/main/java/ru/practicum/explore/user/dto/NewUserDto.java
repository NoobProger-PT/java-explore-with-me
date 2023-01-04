package ru.practicum.explore.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
public class NewUserDto {

    @NotBlank
    private String name;
    @NotNull
    @Email
    private String email;
}
