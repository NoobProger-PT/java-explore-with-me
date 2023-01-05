package ru.practicum.exception.user_exception;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String s) {
        super(s);
    }
}