package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.common.exeptions.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) long userId,
                                         @RequestBody @Valid BookingDtoIn bookingDtoIn) {
        log.info("Creating booking {}, userId {}", bookingDtoIn, userId);
        return bookingClient.create(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER_USER_ID) long userId,
                             @PathVariable long bookingId,
                             @RequestParam Boolean approved) {
        log.info("Update booking ID {} userId {} ", bookingId, userId);
        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER_USER_ID) long userId,
                              @PathVariable(required = false) long bookingId) {
        log.info("Get BookingDto {} userId {} ", bookingId, userId);
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(HEADER_USER_ID) long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                 @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        StateStatus stateParam = StateStatus.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + state));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(HEADER_USER_ID) Long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        StateStatus stateParam = StateStatus.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + state));
        log.info("Get booking owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllByOwner(userId, state, from, size);
    }
}
