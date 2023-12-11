package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.model.BookingDtoForItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NotNull
public class ItemDto {
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Boolean available;
    private Long requestId;
    private BookingDtoForItemDto lastBooking;
    private BookingDtoForItemDto nextBooking;
    private Collection<CommentDto> comments;
}
