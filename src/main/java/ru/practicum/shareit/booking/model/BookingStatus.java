package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.common.exeptions.UnsupportedStatusException;

public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED;

    public static BookingStatus from(String state) {
        for (BookingStatus value : BookingStatus.values()) {
            if (value.name().equals(state)) return value;
        }
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }
}
