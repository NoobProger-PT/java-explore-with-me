package ru.practicum.exception.event_exception;

public class WrongEventDateException extends RuntimeException {
    public WrongEventDateException(String s) {
        super(s);
    }
}
