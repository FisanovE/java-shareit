package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(HEADER_USER_ID) Long userId,
                             @Valid @RequestBody BookingDtoIn bookingDtoIn) {
        log.info("Create BookingDto User {} ", userId);
        return bookingService.create(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(HEADER_USER_ID) Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved) {
        log.info("Update Booking ID {} user {} ", bookingId, userId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(HEADER_USER_ID) Long userId,
                              @PathVariable(required = false) Long bookingId) {
        log.info("Get BookingDto {} user {} ", bookingId, userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllByBooker(@RequestHeader(HEADER_USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state,
                                                 @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                                 @RequestParam(defaultValue = "10", required = false) @Min(1) int size) {
        log.info("Get BookingDtos Booker {} state {} from {} size {}", userId, state, from, size);
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllByOwner(@RequestHeader(HEADER_USER_ID) Long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
                                                @RequestParam(defaultValue = "10", required = false) @Min(1) int size) {
        log.info("Get BookingDtos Owner {} state {} from {} size {}", userId, state, from, size);
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}
