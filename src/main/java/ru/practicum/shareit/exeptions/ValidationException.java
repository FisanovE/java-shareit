package ru.practicum.shareit.exeptions;

public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }
}
