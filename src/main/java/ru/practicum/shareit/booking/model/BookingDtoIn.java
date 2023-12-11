package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoIn {

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
