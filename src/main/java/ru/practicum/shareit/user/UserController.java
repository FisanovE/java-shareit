package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.ValidationMarkers;
import ru.practicum.shareit.user.model.UserDto;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Validated(ValidationMarkers.Create.class) @RequestBody UserDto userDto) {
        log.info("Create user");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @Validated(ValidationMarkers.Update.class) @RequestBody UserDto userDto) {
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
