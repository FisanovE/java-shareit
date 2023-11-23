package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Getter
@Setter
@Builder
public class BookingDtoIn {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
