package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    UserMapper userMapper = new UserMapper();

    @Test
    void toUserDtoForBooking() {
        User user = new User(1L, "name", "user@mail.ru");

        UserDtoForBooking dto = userMapper.toUserDtoForBooking(user);

        assertEquals(user.getId(), dto.getId());
    }

}