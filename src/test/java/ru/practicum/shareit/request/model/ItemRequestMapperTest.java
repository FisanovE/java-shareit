package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @Test
    void toItemRequest() {
        ItemRequestDtoIn dto = new ItemRequestDtoIn("description");

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(dto);

        assertEquals(itemRequest.getDescription(), dto.getDescription());
    }
}