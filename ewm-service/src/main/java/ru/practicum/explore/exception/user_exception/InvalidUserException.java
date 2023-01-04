package ru.practicum.explore.exception.user_exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String s) {
        super(s);
    }
}
