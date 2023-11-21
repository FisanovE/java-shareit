package ru.practicum.shareit.booking.model;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId() != null ? booking.getId() : null)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public Booking toBooking(BookingDtoIn bookingDtoIn) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoIn.getStart());
        booking.setEnd(bookingDtoIn.getEnd());
        return booking;
    }

    public BookingDtoForItem toBookingDtoNew(Booking booking) {
        BookingDtoForItem bookingDtoForItem = new BookingDtoForItem();
        bookingDtoForItem.setId(booking.getId());
        bookingDtoForItem.setBookerId(booking.getBooker().getId());
        return bookingDtoForItem;
    }
}
