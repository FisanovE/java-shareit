package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.common.ValidateService;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.UnsupportedStatusException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.common.Constants.*;

@RequiredArgsConstructor
@Service
public class BookingService {
    private final ValidateService validateService;

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto create(Long userId, BookingDtoIn bookingDtoIn) {
        Item item = itemRepository.findById(bookingDtoIn.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + bookingDtoIn.getItemId()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        if (!item.getAvailable()) throw new ValidationException("Owner banned rent Item: " + bookingDtoIn.getItemId());
        validateService.checkTimeForValid(bookingDtoIn.getStart(), bookingDtoIn.getEnd());
        Booking booking = bookingMapper.toBooking(bookingDtoIn);
        if (Objects.equals(userId, item.getOwner().getId())) throw new NotFoundException("Booker ID: " + userId
                + " not be equal to owner ID: " + item.getOwner());
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(user);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto update(Long userId, Long bookingId, Boolean approved) {
        Booking bookingUpdate = bookingRepository.findByBookingIdAndOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId + " user " + userId));
        validateService.checkMatchingIdUsers(userId, bookingUpdate.getItem().getOwner().getId());
        if (bookingUpdate.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Booking has already been approved");
        }
        if (approved) {
            bookingUpdate.setStatus(BookingStatus.APPROVED);
        } else {
            bookingUpdate.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toBookingDto(bookingRepository.save(bookingUpdate));
    }

    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByBookingIdAndBookerIdOrOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        return bookingMapper.toBookingDto(booking);
    }

    public Collection<BookingDto> getAllByBooker(Long userId, String state) {
        StateStatus status = StateStatus.from(state);

        if (!userRepository.existsById(userId)) throw new NotFoundException("User not found: " + userId);

        switch (status) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId, sortByStartDesc).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(state.toUpperCase()))
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(),
                                LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public Collection<BookingDto> getAllByOwner(Long userId, String state) {
        StateStatus status = StateStatus.from(state);

        if (!userRepository.existsById(userId)) throw new NotFoundException("User not found: " + userId);

        switch (status) {
            case ALL:
                return bookingRepository.findAllByItemOwnerId(userId, sortByStartDesc).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.valueOf(state.toUpperCase()))
                        .stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sortByStartDesc).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), sortByStartDesc).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
