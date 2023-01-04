package ru.practicum.exception.user_exception;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String s) {
        super(s);
    }
}
