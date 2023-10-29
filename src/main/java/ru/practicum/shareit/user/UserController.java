package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public UserDto create(@RequestBody User user) {
        log.info("Create user");
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody User user) {
        log.info("Update user {}", user.getId());
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Delete user {}", id);
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable(required = false) String id) {
        log.info("Get user {}", id);
        return userService.getById(Long.parseLong(id));
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Get users");
        return userService.getAll();
    }
}
