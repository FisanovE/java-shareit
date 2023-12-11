package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoForItemDto;
import ru.practicum.shareit.booking.model.BookingDtoIn;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoIn;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTestIT {

    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingService bookingService;


    private UserDto createUserDto(String name) {
        return new UserDto(null, name, name + "@mail.ru");
    }

    private ItemRequestDtoIn createItemRequestDtoIn() {
        return new ItemRequestDtoIn("RequestDescription");
    }

    private BookingDtoIn createBookingDtoIn(LocalDateTime start, LocalDateTime end, Long itemId) {
        return new BookingDtoIn(start, end, itemId);
    }

    private ItemDto createItemDto(Long requestId, BookingDtoForItemDto lastBooking, BookingDtoForItemDto nextBooking, List<CommentDto> comments) {
        return new ItemDto(null, "itemName", "itemDescription", true, requestId, lastBooking, nextBooking, comments);
    }

    private Comment commentCreated(Long commentId, String text, Item item, User author, LocalDateTime created) {
        return new Comment(commentId, text, item, author, created);
    }


    @Test
    @Sql({"/schema.sql"})
    void shouldReturnAllItemsByUser() throws InterruptedException {
        int from = 0;
        int size = 1;
        UserDto bookerDto = userService.create(createUserDto("booker"));
        UserDto ownerDto = userService.create(createUserDto("owner"));
        ItemRequestDto requestAdded = itemRequestService.create(ownerDto.getId(), createItemRequestDtoIn());
        ItemDto itemDtoCreated = createItemDto(requestAdded.getId(), null, null, null);
        ItemDto itemDtoAdded = itemService.create(ownerDto.getId(), itemDtoCreated);
        BookingDtoIn bookingDtoIn = createBookingDtoIn(LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2), itemDtoAdded.getId());
        BookingDto bookingDto = bookingService.create(bookerDto.getId(), bookingDtoIn);
        Thread.sleep(2000);
        Comment comment = commentCreated(null, "text", itemMapper.toItem(itemDtoAdded),
                userMapper.toUser(bookerDto), LocalDateTime.now());
        CommentDto commentDto = itemService.createComment(bookerDto.getId(), itemDtoAdded.getId(), comment);
        commentDto.setId(commentDto.getId());

        List<ItemDto> list = new ArrayList<>(itemService.getAll(ownerDto.getId(), from, size));

        assertThat(list).isNotEmpty().hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(itemDtoAdded.getId());
        assertThat(list.get(0).getName()).isEqualTo(itemDtoAdded.getName());
        assertThat(list.get(0).getDescription()).isEqualTo(itemDtoAdded.getDescription());
        assertThat(list.get(0).getAvailable()).isEqualTo(itemDtoAdded.getAvailable());
        assertThat(list.get(0).getRequestId()).isEqualTo(itemDtoAdded.getRequestId());
        assertThat(list.get(0).getComments().stream().findFirst().get().getId()).isEqualTo(commentDto.getId());
    }
}