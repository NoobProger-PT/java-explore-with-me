package ru.practicum.explore.exception.category_exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String s) {
        super(s);
    }
}
