package ru.practicum.exception.category_exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String s) {
        super(s);
    }
}