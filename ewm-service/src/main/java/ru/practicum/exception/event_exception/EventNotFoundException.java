package ru.practicum.exception.event_exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String s) {
        super(s);
    }
}