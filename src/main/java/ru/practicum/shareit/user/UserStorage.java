package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {

    UserDto create(User user);

    UserDto update(Long id, User user);

    void delete(Long id);

    Collection<UserDto> getAll();

    UserDto getById(Long id);

    void checkContainsUser(Long id);
}
