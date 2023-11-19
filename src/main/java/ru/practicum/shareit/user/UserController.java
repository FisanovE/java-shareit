package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Create user");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Update user {}", id);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Delete user {}", id);
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable(required = false) Long id) {
        log.info("Get user {}", id);
        return userService.getById(id);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Get users");
        return userService.getAll();
    }
}
