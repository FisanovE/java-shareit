package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
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

    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime current, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime current, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime startBefore,
                                                                       LocalDateTime endAfter, Pageable pageable);

    List<Booking> findAllByItemOwnerId(Long userId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime current, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime current, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime startBefore,
                                                                       LocalDateTime endAfter, Pageable pageable);

    Optional<Booking> findFirstByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(Long itemId, BookingStatus status, LocalDateTime date);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStart(Long itemId, BookingStatus status, LocalDateTime date);

    Optional<Booking> findByItemIdAndStatusAndStartBeforeAndEndAfter(Long itemId, BookingStatus status,
                                                                     LocalDateTime startBefore,
                                                                     LocalDateTime endAfter);
}
