package ru.practicum.exception.participation_exception;

public class ParticipationAlreadyExistsException extends RuntimeException {
    public ParticipationAlreadyExistsException(String s) {
        super(s);
    }
}
