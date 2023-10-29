package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    ItemDto create(Long id, Item item);

    ItemDto update(Long id, Item item);

    void delete(Long id);

    Collection<ItemDto> getAll(Long userId);

    ItemDto getById(Long id);

    void checkContains(Long id);

    Collection<ItemDto> search(String text);
}
