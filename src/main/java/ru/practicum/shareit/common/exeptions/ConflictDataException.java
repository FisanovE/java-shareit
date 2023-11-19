package ru.practicum.shareit.common.exeptions;

public class ConflictDataException extends RuntimeException {

    public ConflictDataException(final String message) {
        super(message);
    }
}
