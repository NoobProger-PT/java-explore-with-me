package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class NewUserDto {

    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
