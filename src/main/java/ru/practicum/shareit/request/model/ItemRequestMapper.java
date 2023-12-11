package ru.practicum.shareit.request.model;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@NoArgsConstructor
@Component
public class ItemRequestMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public ItemRequest toItemRequest(ItemRequestDtoIn itemRequestDtoIn) {
        return ItemRequest.builder()
                .description(itemRequestDtoIn.getDescription())
                .created(LocalDateTime.now())
                .build();
    }
}
