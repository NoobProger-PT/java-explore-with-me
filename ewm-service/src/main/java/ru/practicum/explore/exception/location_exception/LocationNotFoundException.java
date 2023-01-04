package ru.practicum.explore.exception.location_exception;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String s) {
        super(s);
    }
}
