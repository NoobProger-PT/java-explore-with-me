package ru.practicum.event.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.model.Category;
import ru.practicum.event.State;
import ru.practicum.event.location.model.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "event_Date", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_Limit")
    private Integer participantLimit;
    @Column(name = "request_Moderation")
    private Boolean requestModeration;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "confirmed_Requests")
    private int confirmedRequests;
    @Column(name = "created_On")
    private LocalDateTime createdOn;
    @Column(name = "published_On")
    private LocalDateTime publishedOn;
    @Column(name = "state")
    private State state;
    @Column(name = "views")
    private int views;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
