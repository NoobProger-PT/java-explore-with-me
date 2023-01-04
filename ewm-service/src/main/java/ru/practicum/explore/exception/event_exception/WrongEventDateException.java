package ru.practicum.explore.exception.event_exception;

public class WrongEventDateException extends RuntimeException {
    public WrongEventDateException(String s) {
        super(s);
    }
}
