package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.common.exeptions.UnsupportedStatusException;

public enum StateStatus {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static StateStatus from(String state) {
        for (StateStatus value : StateStatus.values()) {
            if (value.name().equals(state)) return value;
        }
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }
}
