package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    Long userId;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        userDto = new UserDto(userId, "Name", "user@mail.ru");
    }

    @Test
    void create_whenUserValid_thenStatus200andUserSavedAndReturned() throws Exception {
        when(userService.create(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.name", Matchers.is("Name")),
                        jsonPath("$.email", Matchers.is("user@mail.ru")));
    }

    @Test
    void update_whenExistsUser_thenStatus200andUserUpdateAndReturned() throws Exception {
        when(userService.update(any(Long.class), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.name", Matchers.is("Name")),
                        jsonPath("$.email", Matchers.is("user@mail.ru")));
    }

    @Test
    void delete_whenExistsUser_thenStatus200andUserDeleted() throws Exception {

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).delete(userId);
    }

    @Test
    void getById_whenExistsUser_thenStatus200andUserReturned() throws Exception {
        when(userService.getById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userId).accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", Matchers.is(1)),
                        jsonPath("$.name", Matchers.is("Name")),
                        jsonPath("$.email", Matchers.is("user@mail.ru")));
    }

    @Test
    void getAll_whenExistsUsers_thenStatus200andUsersReturned() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id", Matchers.is(1)),
                        jsonPath("$[0].name", Matchers.is("Name")),
                        jsonPath("$[0].email", Matchers.is("user@mail.ru")));
    }
}