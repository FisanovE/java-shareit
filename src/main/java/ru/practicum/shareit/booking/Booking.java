package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@Builder
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @Column(name = "item_id", nullable = false)
    private Long item;

    @Column(name = "booker_id", nullable = false)
    private Long booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
