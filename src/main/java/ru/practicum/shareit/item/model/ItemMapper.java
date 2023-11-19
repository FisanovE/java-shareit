package ru.practicum.shareit.item.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId() != null ? item.getId() : null)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId() != null ? itemDto.getId() : null);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
