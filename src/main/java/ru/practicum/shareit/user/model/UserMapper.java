package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public UserDtoForBooking toUserDtoForBooking(User user) {
        UserDtoForBooking userDtoForBooking = new UserDtoForBooking();
        userDtoForBooking.setId(user.getId());
        return userDtoForBooking;
    }

    public User toUser(UserDto userDTO) {
        User user = new User();
        user.setId(userDTO.getId() != null ? userDTO.getId() : null);
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}
