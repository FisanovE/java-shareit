package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.ConflictDataException;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {

    private Long counter = 1L;
    private Map<Long, User> storage = new HashMap<>();
    private final UserMapper mapper;
    private final ItemStorage itemStorage;

    @Override
    public UserDto create(User user) {
        checkContainsEmail(user.getId(), user.getEmail());
        user.setId(counter);
        storage.put(user.getId(), user);
        counter++;
        return mapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, User user) {
        checkContainsUser(id);
        checkContainsEmail(id, user.getEmail());
        User updatedUser = storage.get(id);
        if (user.getName() != null) updatedUser.setName(user.getName());
        if (user.getEmail() != null) updatedUser.setEmail(user.getEmail());
        storage.put(id, updatedUser);
        return mapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        checkContainsUser(id);
        storage.remove(id);
        List<ItemDto> removedItems = (List<ItemDto>) itemStorage.getAll(id);
        for (ItemDto itemDto : removedItems) {
            itemStorage.delete(itemDto.getId());
        }
    }

    @Override
    public Collection<UserDto> getAll() {
        return storage.values().stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getById(Long id) {
        checkContainsUser(id);
        return mapper.toUserDto(storage.get(id));
    }

    public void checkContainsUser(Long id) {
        if (!storage.containsKey(id)) throw new NotFoundException("Invalid User ID:  " + id);
    }

    public void checkContainsEmail(Long id, String email) {
        if (storage.values()
                .stream()
                .anyMatch(userSorted -> (userSorted.getEmail().equals(email) && !userSorted.getId().equals(id))))
            throw new ConflictDataException("This email is already registered:  " + email);
    }
}
