package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        return   ItemDto.builder()
                .id(item.getId() != null ? item.getId() : null)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }

    public Item toItem(ItemDto itemDTO) {
        return Item.builder()
                .id(itemDTO.getId() != null ? itemDTO.getId() : null)
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .available(itemDTO.getAvailable())
                .owner(itemDTO.getOwner())
                .request(itemDTO.getRequest() != null ? itemDTO.getRequest() : null)
                .build();
    }
}
