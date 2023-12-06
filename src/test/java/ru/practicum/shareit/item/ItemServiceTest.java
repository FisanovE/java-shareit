package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.ValidateService;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.common.Constants.SORT_BY_ID_ASC;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ValidateService validateService; // не удалять, для работы void методов ValidateService
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemService itemService;


    @Test
    void create_whenItemValid_thenSavedItem() {
        Long userId = 0L;
        User owner = new User();
        owner.setId(userId);
        Item item = new Item();
        Item savedItem = new Item();
        savedItem.setOwner(owner);
        ItemDto itemDto = new ItemDto();
        when(itemRepository.save(savedItem)).thenReturn(savedItem);
        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemMapper.toItem(itemDto)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto actualItemDto = itemService.create(userId, itemDto);

        verify(itemRepository, times(1)).save(savedItem);
        verify(itemMapper, times(1)).toItem(itemDto);
        verify(itemMapper, times(1)).toItemDto(item);
        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void create_whenUserNotFound_thenReturnException() {
        Long userId = 0L;
        User owner = new User();
        owner.setId(userId);
        Item savedItem = new Item();
        savedItem.setOwner(owner);
        ItemDto itemDto = new ItemDto();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.create(userId, itemDto));

        verify(itemRepository, never()).save(savedItem);
    }

    @Test
    void update_whenItemValid_thenUpdatedItem() {
        Long userId = 0L;
        Long itemId = 1L;
        String itemName = "Name";
        String itemDescription = "description";
        Boolean itemAvailable = true;
        Boolean updatedAvailable = false;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, itemName, itemDescription, itemAvailable, owner, null);
        Item savedItem = new Item(itemId, itemName, itemDescription, updatedAvailable, null, null);
        ItemDto itemDto = new ItemDto(itemId, itemName, itemDescription, itemAvailable, null, null,
                null, null);
        ItemDto itemDtoUpdated = new ItemDto(itemId, itemName, itemDescription, updatedAvailable, null,
                null, null, null);
        itemDto.setAvailable(false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(savedItem);
        when(itemMapper.toItemDto(savedItem)).thenReturn(itemDtoUpdated);
        when(bookingService.getLastBooking(any(Long.class))).thenReturn(null);
        when(bookingService.getNextBooking(any(Long.class))).thenReturn(null);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());


        ItemDto actualItemDto = itemService.update(userId, itemId, itemDto);

        assertEquals(itemDtoUpdated, actualItemDto);
    }

    @Test
    void update_whenNameAndDescriptionAndAvailableIsEmpty_thenUpdatedItem() {
        Long userId = 0L;
        Long itemId = 1L;
        String itemName = "Name";
        String itemDescription = "description";
        Boolean itemAvailable = true;
        Boolean updatedAvailable = false;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, itemName, itemDescription, itemAvailable, owner, null);
        Item savedItem = new Item(itemId, itemName, itemDescription, updatedAvailable, null, null);
        ItemDto itemDto = new ItemDto(itemId, null, null, null, null, null,
                null, null);
        ItemDto itemDtoUpdated = new ItemDto(itemId, itemName, itemDescription, updatedAvailable, null,
                null, null, null);
        itemDto.setAvailable(false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(savedItem);
        when(itemMapper.toItemDto(savedItem)).thenReturn(itemDtoUpdated);
        when(bookingService.getLastBooking(any(Long.class))).thenReturn(null);
        when(bookingService.getNextBooking(any(Long.class))).thenReturn(null);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());


        ItemDto actualItemDto = itemService.update(userId, itemId, itemDto);

        assertEquals(itemDtoUpdated, actualItemDto);
    }

    @Test
    void update_whenUserIsNull_thenReturnException() {
        Long userId = null;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "itemName", "itemDescription", true, null,
                null, null, null);

        assertThrows(ValidationException.class,
                () -> itemService.update(userId, itemId, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenItemIsNull_thenReturnException() {
        Long userId = 0L;
        Long itemId = null;
        ItemDto itemDto = new ItemDto(itemId, "itemName", "itemDescription", true, null,
                null, null, null);

        assertThrows(ValidationException.class,
                () -> itemService.update(userId, itemId, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenItemNotFound_thenReturnException() {
        Long userId = 0L;
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto(itemId, "itemName", "itemDescription", true, null,
                null, null, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.update(userId, itemId, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void delete() {
        Long id = 0L;

        itemService.delete(id);

        verify(itemRepository, times(1)).deleteById(id);
    }

    @Test
    void getById_whenItemFound_thenReturnedItem() {
        Long userId = 0L;
        Long itemId = 1L;
        String itemName = "Name";
        String itemDescription = "description";
        Boolean itemAvailable = true;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, itemName, itemDescription, itemAvailable, owner, null);
        ItemDto itemDto = new ItemDto(itemId, itemName, itemDescription, itemAvailable, null, null, null, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(bookingService.getLastBooking(any(Long.class))).thenReturn(null);
        when(bookingService.getNextBooking(any(Long.class))).thenReturn(null);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());

        ItemDto actualItemDto = itemService.getById(itemId, userId);

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getById_whenItemNotFound_thenReturnException() {
        Long userId = 0L;
        Long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.getById(itemId, userId));
    }

    @Test
    void getById_whenItemFoundOtherUser_thenReturnedItem() {
        Long userId = 0L;
        Long otherUserId = 1L;
        Long itemId = 1L;
        String itemName = "Name";
        String itemDescription = "description";
        Boolean itemAvailable = true;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, itemName, itemDescription, itemAvailable, owner, null);
        ItemDto itemDto = new ItemDto(itemId, itemName, itemDescription, itemAvailable, null, null, null, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());

        ItemDto actualItemDto = itemService.getById(itemId, otherUserId);

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getAll_whenItemFound_thenReturnedItems() {
        Long userId = 0L;
        int from = 2;
        int size = 2;
        final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");
        Pageable sortedById = PageRequest.of(from, size, SORT_BY_ID_ASC);
        User owner = new User(0L, "userName", "user@mail.ru");
        Item item = new Item(1L, "itemName", "description", true, owner, null);
        List<Item> items = List.of(item);
        ItemDto itemDto = new ItemDto(1L, "itemName", "description", true, null, null, null, null);
        List<ItemDto> itemDtos = List.of(itemDto);

        when(itemRepository.findItemsByOwnerId(userId, sortedById)).thenReturn(items);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(bookingService.getLastBooking(any(Long.class))).thenReturn(null);
        when(bookingService.getNextBooking(any(Long.class))).thenReturn(null);

        List<ItemDto> actualItemDto = (List<ItemDto>) itemService.getAll(userId, from, size);

        assertEquals(itemDtos, actualItemDto);
    }

    @Test
    void search_whenTextNotEmpty_thenReturnedItems() {
        int from = 2;
        int size = 2;
        String text = "blah-blah-blah";
        final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");
        Pageable sortedById = PageRequest.of(from, size, SORT_BY_ID_ASC);
        User owner = new User(0L, "userName", "user@mail.ru");
        Item item = new Item(1L, "itemName", "blah-blah-blah", true, owner, null);
        List<Item> items = List.of(item);
        ItemDto itemDto = new ItemDto(1L, "itemName", "blah-blah-blah", true, null, null, null, null);
        List<ItemDto> itemDtos = List.of(itemDto);

        when(itemRepository.search(text, sortedById)).thenReturn(items);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> actualItemDto = (List<ItemDto>) itemService.search(text, from, size);

        assertEquals(itemDtos, actualItemDto);
    }

    @Test
    void search_whenTextIsEmpty_thenReturnedEmptyList() {
        int from = 2;
        int size = 2;
        String text = "";

        List<ItemDto> actualItemDto = (List<ItemDto>) itemService.search(text, from, size);

        assertTrue(actualItemDto.isEmpty());
    }

    @Test
    void createComment_whenTextNotEmpty_thenReturnedComment() {
        Long userId = 0L;
        Long itemId = 1L;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, "itemName", "blah-blah-blah", true, owner, null);
        Booking booking = new Booking();
        Comment comment = new Comment();
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setText("blah-blah-blah");
        CommentDto commentDto = new CommentDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBefore(any(Long.class), any(Long.class),
                any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto actualCommentDto = itemService.createComment(userId, itemId, comment);

        assertEquals(commentDto, actualCommentDto);
    }

    @Test
    void createComment_whenBookingNotFound_thenReturnedException() {
        Long userId = 0L;
        Long itemId = 1L;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, "itemName", "blah-blah-blah", true, owner, null);
        Comment comment = new Comment();
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setText("blah-blah-blah");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBefore(any(Long.class), any(Long.class),
                any(LocalDateTime.class))).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(userId, itemId, comment));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenItemNotFound_thenReturnedException() {
        Long userId = 0L;
        Long itemId = 1L;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, "itemName", "blah-blah-blah", true, owner, null);
        Comment comment = new Comment();
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setText("blah-blah-blah");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(userId, itemId, comment));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenUserNotFound_thenReturnedException() {
        Long userId = 0L;
        Long itemId = 1L;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, "itemName", "blah-blah-blah", true, owner, null);
        Comment comment = new Comment();
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setText("blah-blah-blah");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(userId, itemId, comment));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenTextIsEmpty_thenReturnedException() {
        Long userId = 0L;
        Long itemId = 1L;
        User owner = new User(userId, "userName", "user@mail.ru");
        Item item = new Item(itemId, "itemName", "blah-blah-blah", true, owner, null);
        Comment comment = new Comment();
        comment.setAuthor(owner);
        comment.setItem(item);
        comment.setText("");

        assertThrows(ValidationException.class,
                () -> itemService.createComment(userId, itemId, comment));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void getItemsByRequestId() {
        Long itemRequestId = 0L;
        List<Item> items = List.of(new Item());

        when(itemRepository.findAllByRequestId(itemRequestId, SORT_BY_ID_ASC)).thenReturn(items);

        List<Item> actualItems = itemService.getItemsByRequestId(itemRequestId);

        assertEquals(items, actualItems);
    }

}