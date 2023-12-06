package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingDtoForItemDto;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDtoForItemDto lastBooking;
    private BookingDtoForItemDto nextBooking;
    private Collection<CommentDto> comments;
}
