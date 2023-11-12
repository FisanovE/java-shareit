package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {
        return   UserDto.builder()
                .id(user.getId() != null ? user.getId() : null)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(UserDto userDTO) {
        User user = new User();
        user.setId(userDTO.getId() != null ? userDTO.getId() : null);
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}
