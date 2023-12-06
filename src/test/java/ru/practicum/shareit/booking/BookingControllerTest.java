package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoIn;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateStatus;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;
    Long userId;
    Long itemId;
    Long bookingId;
    BookingDtoIn bookingDtoIn;
    BookingDto bookingDto;
    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    void setUp() {
        userId = 0L;
        itemId = 1L;
        bookingId = 2L;
        start = LocalDateTime.of(2023, 12, 3, 19, 53,17);
        end = start.plusMinutes(1);
        bookingDtoIn = new BookingDtoIn(start, end, itemId);
        bookingDto = new BookingDto(bookingId, start, end, BookingStatus.APPROVED, null, null);
    }

    @Test
    void create_whenBookingValid_thenStatus200andBookingSavedAndReturned() throws Exception {
        when(bookingService.create(anyLong(), any(BookingDtoIn.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(2)),
                        jsonPath("$.start", Matchers.is(start.toString())),
                        jsonPath("$.end", Matchers.is(end.toString())),
                        jsonPath("$.status", Matchers.is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void update_whenBookingValid_thenStatus200andBookingSavedAndReturned() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, true)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(2)),
                        jsonPath("$.start", Matchers.is(start.toString())),
                        jsonPath("$.end", Matchers.is(end.toString())),
                        jsonPath("$.status", Matchers.is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void getById_whenExistsBooking_thenStatus200andBookingReturned() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(2)),
                        jsonPath("$.start", Matchers.is(start.toString())),
                        jsonPath("$.end", Matchers.is(end.toString())),
                        jsonPath("$.status", Matchers.is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void getAllByBooker_whenExistsBookings_thenStatus200andBookingsReturned() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));
        int from = 0;
        int size = 10;
        String state = "ALL";
        mockMvc.perform(get("/bookings?state={state}&from={from}&size={size}", state, from, size)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", Matchers.is(2)),
                        jsonPath("$[0].start", Matchers.is(start.toString())),
                        jsonPath("$[0].end", Matchers.is(end.toString())),
                        jsonPath("$[0].status", Matchers.is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void getAllByOwner_whenExistsBookings_thenStatus200andBookingsReturned() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDto));
        int from = 0;
        int size = 10;
        String state = "ALL";
        mockMvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", state, from, size)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", Matchers.is(2)),
                        jsonPath("$[0].start", Matchers.is(start.toString())),
                        jsonPath("$[0].end", Matchers.is(end.toString())),
                        jsonPath("$[0].status", Matchers.is(BookingStatus.APPROVED.toString())));
    }
}