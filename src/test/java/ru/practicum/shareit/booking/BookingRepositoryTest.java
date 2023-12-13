package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.common.Constants.SORT_BY_ID_ASC;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    Long bookerId;
    Long ownerId;
    Long itemId;
    Long bookingId;
    LocalDateTime start;
    LocalDateTime end;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        User owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));
        ownerId = owner.getId();
        User booker = userRepository.save(new User(null, "booker", "booker@mail.ru"));
        bookerId = booker.getId();
        Item item = itemRepository.save(new Item(null, "Name", "description", true,
                owner, null));
        itemId = item.getId();
                start = LocalDateTime.of(2023, 12, 3, 19, 53,17);
        end = start.plusMinutes(1);
        Booking booking = bookingRepository.save(new Booking(bookingId, start, end, item, booker,
                BookingStatus.APPROVED));
        bookingId = booking.getId();
        int from = 0;
        int size = 1;
        pageable = PageRequest.of(from / size, size, SORT_BY_ID_ASC);
    }

    @Test
    void findByBookingIdAndOwnerId() {
        Optional<Booking> actualBooking = bookingRepository.findByBookingIdAndOwnerId(bookingId, ownerId);

        assertTrue(actualBooking.isPresent());
    }

    @Test
    void findByBookingIdAndBookerIdOrOwnerId() {
        Optional<Booking> actualBooking = bookingRepository.findByBookingIdAndBookerIdOrOwnerId(bookingId, ownerId);

        assertTrue(actualBooking.isPresent());
    }

    @Test
    void findAllByBookerId() {

        List<Booking> actualBookings = bookingRepository.findAllByBookerId(bookerId, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndStatus() {
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.APPROVED, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndEndIsBefore() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 20, 53,17);
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndEndIsBefore(bookerId, currentTime, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndStartIsAfter() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 18, 53,17);
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStartIsAfter(bookerId, currentTime, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 19, 53,30);
        List<Booking> actualBookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId,
                currentTime, currentTime, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerId() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerId(ownerId, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findAllByItemOwnerIdAndStatus() {
        List<Booking> actualBookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.APPROVED, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findByItemOwnerIdAndEndIsBefore() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 20, 53,17);
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(ownerId, currentTime, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findByItemOwnerIdAndStartIsAfter() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 18, 53,17);
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(ownerId, currentTime, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findByItemOwnerIdAndStartIsBeforeAndEndIsAfter() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 19, 53,30);
        List<Booking> actualBookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId,
                currentTime, currentTime, pageable);

        assertFalse(actualBookings.isEmpty());
    }

    @Test
    void findFirstByBookerIdAndItemIdAndEndIsBefore() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 20, 53,17);
        Optional<Booking> actualBooking = bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBefore(bookerId, itemId,
                currentTime);

        assertTrue(actualBooking.isPresent());
    }

    @Test
    void findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 20, 53,17);
        Optional<Booking> actualBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(itemId,
                BookingStatus.APPROVED, currentTime);

        assertTrue(actualBooking.isPresent());
    }

    @Test
    void findFirstByItemIdAndStatusAndStartAfterOrderByStart() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 18, 53,17);
        Optional<Booking> actualBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(itemId,
                BookingStatus.APPROVED, currentTime);

        assertTrue(actualBooking.isPresent());
    }

    @Test
    void findByItemIdAndStatusAndStartBeforeAndEndAfter() {
        LocalDateTime currentTime = LocalDateTime.of(2023, 12, 3, 19, 53,30);
        Optional<Booking> actualBooking = bookingRepository.findByItemIdAndStatusAndStartBeforeAndEndAfter(itemId,
                BookingStatus.APPROVED, currentTime, currentTime);

        assertTrue(actualBooking.isPresent());
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}