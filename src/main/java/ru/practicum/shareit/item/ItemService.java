package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.ValidateService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage storage;
    private final ValidateService validateService;
    private final UserStorage userStorage;

    public ItemDto create(Long userId, Item item) {
        validateService.checkItem(item);
        return storage.create(userId, item);
    }

    public ItemDto update(Long userId, Long id, Item item) {
        validateService.checkMatchingIdUsers(userId, getById(id).getOwner());
        userStorage.checkContainsUser(userId);
        return storage.update(id, item);
    }

    public void delete(Long id) {
        storage.delete(id);
    }

    public ItemDto getById(Long id) {
        return storage.getById(id);
    }

    public Collection<ItemDto> getAll(Long userId) {
        return storage.getAll(userId);
    }

    public Collection<ItemDto> search(String text) {
        return storage.search(text);
    }
}
