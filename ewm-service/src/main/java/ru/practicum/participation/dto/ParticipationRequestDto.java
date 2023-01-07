package ru.practicum.participation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.participation.Status;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ParticipationRequestDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private Status status;
}
