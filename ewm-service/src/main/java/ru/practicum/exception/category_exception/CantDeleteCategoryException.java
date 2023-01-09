package ru.practicum.exception.category_exception;

public class CantDeleteCategoryException extends RuntimeException {
    public CantDeleteCategoryException(String s) {
        super(s);
    }
}
