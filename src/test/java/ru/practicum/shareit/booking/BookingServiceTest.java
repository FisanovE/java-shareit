package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.common.Constants.SORT_BY_ID_ASC;
import static ru.practicum.shareit.common.Constants.SORT_BY_START_DESC;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ValidateService validateService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void create_whenBookingValid_thenSavedBooking() {
        Long ownerId = 0L;
        Long bookerId = 1L;
        Long itemId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        User booker = new User();
        booker.setId(bookerId);
        Item item = new Item();
        item.setAvailable(true);
        item.setOwner(owner);
        Booking booking = new Booking();
        BookingDtoIn bookingDtoIn = new BookingDtoIn(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1L), itemId);
        BookingDto bookingDto = new BookingDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingMapper.toBooking(bookingDtoIn)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto actualBookingDto = bookingService.create(bookerId, bookingDtoIn);

        assertEquals(bookingDto, actualBookingDto);
    }

    @Test
    void create_whenBookerIdEqualOwnerId_thenNotSavedBooking() {
        Long ownerId = 0L;
        Long itemId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        User booker = new User();
        booker.setId(ownerId);
        Item item = new Item();
        item.setAvailable(true);
        item.setOwner(owner);
        Booking booking = new Booking();
        BookingDtoIn bookingDtoIn = new BookingDtoIn(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1L), itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(booker));
        when(bookingMapper.toBooking(bookingDtoIn)).thenReturn(booking);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(ownerId, bookingDtoIn));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void create_whenAvailableIsFalse_thenReturnException() {
        Long ownerId = 0L;
        Long itemId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        User booker = new User();
        booker.setId(ownerId);
        Item item = new Item();
        item.setAvailable(false);
        item.setOwner(owner);
        Booking booking = new Booking();
        BookingDtoIn bookingDtoIn = new BookingDtoIn(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1L), itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class,
                () -> bookingService.create(ownerId, bookingDtoIn));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void create_whenUserNotFound_thenReturnException() {
        Long ownerId = 0L;
        Long itemId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        User booker = new User();
        booker.setId(ownerId);
        Item item = new Item();
        Booking booking = new Booking();
        BookingDtoIn bookingDtoIn = new BookingDtoIn(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1L), itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(ownerId, bookingDtoIn));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void create_whenItemNotFound_thenReturnException() {
        Long ownerId = 0L;
        Long itemId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        User booker = new User();
        booker.setId(ownerId);
        Booking booking = new Booking();
        BookingDtoIn bookingDtoIn = new BookingDtoIn(LocalDateTime.now(), LocalDateTime.now().plusMinutes(1L), itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.create(ownerId, bookingDtoIn));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void update_whenApprovedIsTrue_thenSavedBooking() {
        Long userId = 0L;
        Long bookingId = 1L;
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        Boolean approved = true;
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        BookingDto bookingDto = new BookingDto();

        when(bookingRepository.findByBookingIdAndOwnerId(bookingId, userId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.save(booking)).thenReturn(booking);


        BookingDto actualBookingDto = bookingService.update(userId, bookingId, approved);

        assertEquals(bookingDto, actualBookingDto);
    }

    @Test
    void update_whenBookingNotFound_thenReturnException() {
        Long userId = 0L;
        Long bookingId = 1L;
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        Boolean approved = true;
        Booking booking = new Booking();

        when(bookingRepository.findByBookingIdAndOwnerId(bookingId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.update(userId, bookingId, approved));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void update_whenApprovedIsFalse_thenSavedBooking() {
        Long userId = 0L;
        Long bookingId = 1L;
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        Boolean approved = false;
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        BookingDto bookingDto = new BookingDto();

        when(bookingRepository.findByBookingIdAndOwnerId(bookingId, userId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.save(booking)).thenReturn(booking);


        BookingDto actualBookingDto = bookingService.update(userId, bookingId, approved);

        assertEquals(bookingDto, actualBookingDto);
    }

    @Test
    void update_whenBookingStatusIsApproved_thenNotSavedBooking() {
        Long userId = 0L;
        Long bookingId = 1L;
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setOwner(owner);
        Boolean approved = true;
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findByBookingIdAndOwnerId(bookingId, userId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.update(userId, bookingId, approved));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getById_whenBookingExists_thenReturnBooking() {
        Long userId = 0L;
        Long bookingId = 1L;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();

        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findByBookingIdAndBookerIdOrOwnerId(bookingId, userId)).thenReturn(Optional.of(booking));

        BookingDto actualBookingDto = bookingService.getById(bookingId, userId);

        assertEquals(bookingDto, actualBookingDto);
    }

    @Test
    void getById_whenBookingNotFound_thenReturnException() {
        Long userId = 0L;
        Long bookingId = 1L;

        when(bookingRepository.findByBookingIdAndBookerIdOrOwnerId(bookingId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, userId));
    }

    @Test
    void getAllByBooker_whenUserNotFound_thenReturnException() {
        Long userId = 0L;
        String state = "ALL";
        int from = 1;
        int size = 1;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllByBooker(userId, state, from, size));
    }

    @Test
    void getAllByBooker_whenStateIsAll_thenReturnBookings() {
        Long userId = 0L;
        String state = "ALL";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByBookerId(userId, PageRequest.of(from / size, size, SORT_BY_START_DESC)))
                .thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByBooker(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByBooker_whenStateIsWaiting_thenReturnBookings() {
        Long userId = 0L;
        String state = "WAITING";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(state.toUpperCase()),
                PageRequest.of(from / size, size, SORT_BY_ID_ASC))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByBooker(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByBooker_whenStateIsRejected_thenReturnBookings() {
        Long userId = 0L;
        String state = "REJECTED";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.valueOf(state.toUpperCase()),
                PageRequest.of(from / size, size, SORT_BY_ID_ASC))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByBooker(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByBooker_whenStateIsPast_thenReturnBookings() {
        Long userId = 0L;
        String state = "PAST";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByBookerIdAndEndIsBefore(any(Long.class), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByBooker(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByBooker_whenStateIsFuture_thenReturnBookings() {
        Long userId = 0L;
        String state = "FUTURE";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByBookerIdAndStartIsAfter(any(Long.class), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByBooker(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByBooker_whenStateIsCurrent_thenReturnBookings() {
        Long userId = 0L;
        String state = "CURRENT";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(any(Long.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(PageRequest.class))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByBooker(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByBooker_whenStateIsUnknown_thenReturnException() {
        Long userId = 0L;
        String state = "Unknown";
        int from = 1;
        int size = 1;

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllByBooker(userId, state, from, size));
    }

    @Test
    void getAllByOwner_whenUserNotFound_thenReturnException() {
        Long userId = 0L;
        String state = "ALL";
        int from = 1;
        int size = 1;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllByOwner(userId, state, from, size));
    }

    @Test
    void getAllByOwner_whenStateIsAll_thenReturnBookings() {
        Long userId = 0L;
        String state = "ALL";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByItemOwnerId(userId, PageRequest.of(from, size, SORT_BY_START_DESC)))
                .thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByOwner(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByOwner_whenStateIsWaiting_thenReturnBookings() {
        Long userId = 0L;
        String state = "WAITING";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.valueOf(state.toUpperCase()),
                PageRequest.of(from, size, SORT_BY_ID_ASC))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByOwner(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByOwner_whenStateIsRejected_thenReturnBookings() {
        Long userId = 0L;
        String state = "REJECTED";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.valueOf(state.toUpperCase()),
                PageRequest.of(from, size, SORT_BY_ID_ASC))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByOwner(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByOwner_whenStateIsPast_thenReturnBookings() {
        Long userId = 0L;
        String state = "PAST";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findByItemOwnerIdAndEndIsBefore(any(Long.class), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByOwner(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByOwner_whenStateIsFuture_thenReturnBookings() {
        Long userId = 0L;
        String state = "FUTURE";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findByItemOwnerIdAndStartIsAfter(any(Long.class), any(LocalDateTime.class),
                any(PageRequest.class))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByOwner(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByOwner_whenStateIsCurrent_thenReturnBookings() {
        Long userId = 0L;
        String state = "CURRENT";
        int from = 1;
        int size = 1;
        Booking booking = new Booking();
        BookingDto bookingDto = new BookingDto();
        List<Booking> bookings = List.of(booking);
        List<BookingDto> bookingDtos = List.of(bookingDto);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(bookingMapper.toBookingDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(any(Long.class), any(LocalDateTime.class),
                any(LocalDateTime.class), any(PageRequest.class))).thenReturn(bookings);

        List<BookingDto> actualBookingDtos = (List<BookingDto>) bookingService.getAllByOwner(userId, state, from, size);

        assertEquals(bookingDtos, actualBookingDtos);
    }

    @Test
    void getAllByOwner_whenStateIsUnknown_thenReturnException() {
        Long userId = 0L;
        String state = "Unknown";
        int from = 1;
        int size = 1;

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllByOwner(userId, state, from, size));
    }

    @Test
    void getLastBooking_whenLastBookingActionIsEmpty_thenReturnDto() {
        Long itemId = 0L;
        Booking booking = new Booking();
        BookingDtoForItemDto dto = new BookingDtoForItemDto();

        when(bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(any(Long.class),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingRepository.findByItemIdAndStatusAndStartBeforeAndEndAfter(any(Long.class),
                any(BookingStatus.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDtoForItemDto(booking)).thenReturn(dto);

        BookingDtoForItemDto actualDto = bookingService.getLastBooking(itemId);

        assertEquals(dto, actualDto);
    }

    @Test
    void getLastBooking_whenLastBookingActionIsNotEmpty_thenReturnDto() {
        Long itemId = 0L;
        Booking booking = new Booking();
        BookingDtoForItemDto dto = new BookingDtoForItemDto();

        when(bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(any(Long.class),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingRepository.findByItemIdAndStatusAndStartBeforeAndEndAfter(any(Long.class),
                any(BookingStatus.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Optional.empty());
        when(bookingMapper.toBookingDtoForItemDto(booking)).thenReturn(dto);

        BookingDtoForItemDto actualDto = bookingService.getLastBooking(itemId);

        assertEquals(dto, actualDto);
    }

    @Test
    void getNextBooking() {
        Long itemId = 0L;
        Booking booking = new Booking();
        BookingDtoForItemDto dto = new BookingDtoForItemDto();

        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(any(Long.class),
                any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingDtoForItemDto(booking)).thenReturn(dto);

        BookingDtoForItemDto actualDto = bookingService.getNextBooking(itemId);

        assertEquals(dto, actualDto);
    }
}