package ru.practicum.shareit.booking.model;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId() != null ? booking.getId() : null);
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setItem(booking.getItem());
        return bookingDto;
    }

    public Booking toBooking(BookingDtoIn bookingDtoIn) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoIn.getStart());
        booking.setEnd(bookingDtoIn.getEnd());
        return booking;
    }

    public BookingDtoForItemDto toBookingDtoForItemDto(Booking booking) {
        BookingDtoForItemDto bookingDtoForItemDto = new BookingDtoForItemDto();
        bookingDtoForItemDto.setId(booking.getId());
        bookingDtoForItemDto.setBookerId(booking.getBooker().getId());
        return bookingDtoForItemDto;
    }
}
