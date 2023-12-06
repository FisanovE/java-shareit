package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.UserController;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static java.awt.SystemColor.text;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    Long userId;
    Long itemId;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userId = 0L;
        itemId = 1L;
        itemDto = new ItemDto(itemId, "Name", "description", true, null, null,
                null, null);
    }

    @Test
    void create_whenItemValid_thenStatus200andItemSavedAndReturned() throws Exception {
        when(itemService.create(userId, itemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.name", Matchers.is("Name")),
                        jsonPath("$.description", Matchers.is("description")),
                        jsonPath("$.available", Matchers.is(true)));
    }

    @Test
    void update_whenItemValid_thenStatus200andItemSavedAndReturned() throws Exception {
        when(itemService.update(userId, itemId, itemDto)).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.name", Matchers.is("Name")),
                        jsonPath("$.description", Matchers.is("description")),
                        jsonPath("$.available", Matchers.is(true)));
    }

    @Test
    void delete_whenExistsItem_thenStatus200andItemDeleted() throws Exception {
        mockMvc.perform(delete("/items/{id}", itemId))
                .andExpect(status().isOk());

        verify(itemService).delete(itemId);
    }

    @Test
    void getById_whenExistsItem_thenStatus200andItemReturned() throws Exception {
        when(itemService.getById(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.name", Matchers.is("Name")),
                        jsonPath("$.description", Matchers.is("description")),
                        jsonPath("$.available", Matchers.is(true)));
    }

    @Test
    void getAll_whenExistsItems_thenStatus200andItemsReturned() throws Exception {
        int from = 1;
        int size = 5;
        when(itemService.getAll(userId, from, size)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items?from={from}&size={size}", from, size)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id", Matchers.is(1)),
                        jsonPath("$[0].name", Matchers.is("Name")),
                        jsonPath("$[0].description", Matchers.is("description")),
                        jsonPath("$[0].available", Matchers.is(true)));

    }

    @Test
    void search_whenExistsItems_thenStatus200andItemsReturned() throws Exception {
        int from = 0;
        int size = 10;
        String text = "дРелЬ";
        when(itemService.search(text, from, size)).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search?from={from}&size={size}&text={text}", from, size, text)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id", Matchers.is(1)),
                        jsonPath("$[0].name", Matchers.is("Name")),
                        jsonPath("$[0].description", Matchers.is("description")),
                        jsonPath("$[0].available", Matchers.is(true)));
    }

    @Test
    void createComment_whenCommentValid_thenStatus200andCommentReturned() throws Exception {
        LocalDateTime now = LocalDateTime.of(2023, 12, 3, 19, 53,17);
        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto(1L, "blah-blah-blah", "authorName", now);
        when(itemService.createComment(anyLong(), anyLong(), any(Comment.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.text", Matchers.is("blah-blah-blah")),
                        jsonPath("$.authorName", Matchers.is("authorName")),
                        jsonPath("$.created", Matchers.is(now.toString())));
    }
}