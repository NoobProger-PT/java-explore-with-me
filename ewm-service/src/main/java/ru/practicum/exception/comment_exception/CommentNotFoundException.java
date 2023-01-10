package ru.practicum.exception.comment_exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(String s) {
        super(s);
    }
}
