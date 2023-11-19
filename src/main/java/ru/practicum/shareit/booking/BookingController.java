package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoIn;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String headerUserId = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(headerUserId) Long userId,
                             @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Create BookingDto User {} ", userId);
        return bookingService.create(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(headerUserId) Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved) {
        log.info("Update Booking ID {} user {} ", bookingId, userId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(headerUserId) Long userId,
                              @PathVariable(required = false) Long bookingId) {
        log.info("Get BookingDto {} user {} ", bookingId, userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllByBooker(@RequestHeader(headerUserId) Long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Get BookingDtos Booker {} state {}", userId, state);
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllByOwner(@RequestHeader(headerUserId) Long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Get BookingDtos Owner {} state {}", userId, state);
        return bookingService.getAllByOwner(userId, state);
    }
}
