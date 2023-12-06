package ru.practicum.shareit.booking.model;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoIn {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
