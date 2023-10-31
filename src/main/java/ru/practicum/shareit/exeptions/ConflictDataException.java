package ru.practicum.shareit.exeptions;

public class ConflictDataException extends RuntimeException {

    public ConflictDataException(final String message) {
        super(message);
    }
}
