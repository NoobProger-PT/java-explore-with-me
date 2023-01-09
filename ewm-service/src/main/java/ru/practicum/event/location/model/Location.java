package ru.practicum.event.location.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Location {
    private float lat;
    private float lon;
}
