package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private final UserStorage userStorage;

    Long counter = 1L;
    private Map<Long, Item> storage = new HashMap<>();
    private final ItemMapper mapper;

    @Override
    public ItemDto create(Long userId, Item item) {
        userStorage.checkContainsUser(userId);
        item.setId(counter);
        item.setOwner(userId);
        storage.put(item.getId(), item);
        counter++;
        return mapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long id, Item item) {
        checkContains(id);
        Item itemSource = storage.get(id);
        if (item.getName() != null) itemSource.setName(item.getName());
        if (item.getDescription() != null) itemSource.setDescription(item.getDescription());
        if (item.getAvailable() != null) itemSource.setAvailable(item.getAvailable());
        storage.put(itemSource.getId(), itemSource);
        return mapper.toItemDto(itemSource);
    }

    @Override
    public void delete(Long id) {
        checkContains(id);
        storage.remove(id);
    }

    @Override
    public Collection<ItemDto> getAll(Long userId) {
        return storage.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(mapper::toItemDto)
                .collect(toList());
    }

    @Override
    public ItemDto getById(Long id) {
        checkContains(id);
        return mapper.toItemDto(storage.get(id));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        if (text.isEmpty() || text.isBlank()) return new ArrayList<>();
        return storage.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable())
                .map(mapper::toItemDto)
                .collect(toList());
    }

    public void checkContains(Long id) {
        if (!storage.containsKey(id)) {
            throw new NotFoundException("Invalid Item ID:  " + id);
        }
    }
}
