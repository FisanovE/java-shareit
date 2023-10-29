package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.ValidateService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final ValidateService validateService;

    public UserDto create(User user) {
        validateService.checkingNameForValid(user);
        validateService.checkingEmailForValid(user);
        return userStorage.create(user);
    }

    public UserDto update(Long id, User user) {
        if (user.getName() != null) validateService.checkingNameForValid(user);
        if (user.getEmail() != null) validateService.checkingEmailForValid(user);
        return userStorage.update(id, user);
    }

    public void delete(Long id) {
        userStorage.delete(id);
    }

    public UserDto getById(Long id) {
        return userStorage.getById(id);
    }

    public Collection<UserDto> getAll() {
        return userStorage.getAll();
    }

}
