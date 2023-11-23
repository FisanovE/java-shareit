package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.id = ?1 AND b.item.owner.id = ?2")
    Optional<Booking> findByBookingIdAndOwnerId(Long bookingId, Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.id = ?1 AND (b.item.owner.id = ?2 OR b.booker.id = ?2)")
    Optional<Booking> findByBookingIdAndBookerIdOrOwnerId(Long bookingId, Long userId);

    Collection<Booking> findAllByBookerId(Long userId, Sort sort);

    Collection<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status);

    Collection<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime current, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime current, Sort sort);

    Collection<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime startBefore, LocalDateTime endAfter);

    Collection<Booking> findAllByItemOwnerId(Long userId, Sort sort);

    Collection<Booking> findAllByItemOwnerIdAndStatus(Long userId, BookingStatus status);

    Collection<Booking> findByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime current, Sort sort);

    Collection<Booking> findByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime current, Sort sort);

    Collection<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime startBefore, LocalDateTime endAfter);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(Long itemId, BookingStatus status, LocalDateTime date);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStart(Long itemId, BookingStatus status, LocalDateTime date);

    Optional<Booking> findByItemIdAndStatusAndStartBeforeAndEndAfter(Long itemId, BookingStatus status,
                                                                     LocalDateTime startBefore,
                                                                     LocalDateTime endAfter);
}
