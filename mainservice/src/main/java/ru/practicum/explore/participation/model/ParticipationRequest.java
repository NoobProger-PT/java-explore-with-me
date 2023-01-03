package ru.practicum.explore.participation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.participation.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participation")
@Getter
@Setter
@NoArgsConstructor
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "event_id")
    private Long event;
    @Column(name = "requester_id")
    private Long requester;
    @Column(name = "status")
    private Status status;
}
