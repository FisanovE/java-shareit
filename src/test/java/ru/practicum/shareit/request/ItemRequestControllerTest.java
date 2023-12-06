package ru.practicum.shareit.request;

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
import ru.practicum.shareit.booking.model.BookingDtoIn;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoIn;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;
    ItemRequestDtoIn itemRequestDtoIn;
    ItemRequestDto itemRequestDto;
    LocalDateTime now;
    Long requestId;
    Long userId;

    @BeforeEach
    void setUp() {
        userId = 0L;
        requestId = 1L;
        now = LocalDateTime.of(2023, 12, 3, 19, 53,17);
        itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDto = new ItemRequestDto(requestId, "description", now, null);
    }

    @Test
    void create_whenRequestValid_thenStatus200andRequestSavedAndReturned() throws Exception {
        when(itemRequestService.create(anyLong(), any(ItemRequestDtoIn.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.description", Matchers.is("description")),
                        jsonPath("$.created", Matchers.is(now.toString())));
    }

    @Test
    void getAllByUser_whenExistsRequests_thenStatus200andRequestsReturned() throws Exception {
        when(itemRequestService.getAllByUser(anyLong())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", Matchers.is(1)),
                        jsonPath("$[0].description", Matchers.is("description")),
                        jsonPath("$[0].created", Matchers.is(now.toString())));
    }

    @Test
    void getAll_whenExistsRequests_thenStatus200andRequestsReturned() throws Exception {
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));
        int from = 0;
        int size = 10;
        mockMvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", Matchers.is(1)),
                        jsonPath("$[0].description", Matchers.is("description")),
                        jsonPath("$[0].created", Matchers.is(now.toString())));
    }

    @Test
    void getById_whenExistsRequest_thenStatus200andRequestReturned() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDtoIn)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.description", Matchers.is("description")),
                        jsonPath("$.created", Matchers.is(now.toString())));
    }
}