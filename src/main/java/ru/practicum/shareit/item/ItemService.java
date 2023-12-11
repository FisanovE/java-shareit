package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDtoForItemDto;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.common.Constants.SORT_BY_ID_ASC;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Item item = itemMapper.toItem(itemDto);
        item.setOwner(user);
        if (itemDto.getRequestId() != null) item.setRequestId(itemDto.getRequestId());
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) throw new ValidationException("User Id must not be empty");
        if (itemId == null) throw new ValidationException("Item Id must not be empty");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (!Objects.equals(userId, item.getOwner().getId()))
            throw new NotFoundException("ID: " + userId + " is not equal to owner ID: " + item.getOwner().getId());
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        ItemDto finalItemDto = itemMapper.toItemDto(itemRepository.save(item));
        finalItemDto.setLastBooking(getLastBooking(finalItemDto.getId()));
        finalItemDto.setNextBooking(getNextBooking(finalItemDto.getId()));
        finalItemDto.setComments(getCommentsByItemDTO(finalItemDto.getId()));

        return finalItemDto;
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public ItemDto getById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            itemDto.setLastBooking(getLastBooking(itemDto.getId()));
            itemDto.setNextBooking(getNextBooking(itemDto.getId()));
        } else {
            itemDto.setNextBooking(null);
        }
        itemDto.setComments(getCommentsByItemDTO(itemDto.getId()));
        return itemDto;
    }

    public Collection<ItemDto> getAll(Long userId, int from, int size) {
        Pageable sortedById = PageRequest.of(from, size, SORT_BY_ID_ASC);
        return itemRepository.findItemsByOwnerId(userId, sortedById).stream()
                .map(itemMapper::toItemDto)
                .peek(itemDto -> itemDto.setLastBooking(getLastBooking(itemDto.getId())))
                .peek(itemDto -> itemDto.setNextBooking(getNextBooking(itemDto.getId())))
                .peek(itemDto -> itemDto.setComments(getCommentsByItemDTO(itemDto.getId())))
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> search(String text, int from, int size) {
        if (text.isEmpty()) return new ArrayList<>();
        Pageable sortedById = PageRequest.of(from, size, SORT_BY_ID_ASC);
        return itemRepository.search(text, sortedById).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public CommentDto createComment(Long userId, Long itemId, Comment comment) {
        if (comment.getText().isBlank()) throw new ValidationException("Comment must not be empty");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException("Booking not found"));
        comment.setAuthor(user);
        comment.setItem(item);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    public List<CommentDto> getCommentsByItemDTO(Long id) {
        return commentRepository.findAllByItemId(id).stream().map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public List<Item> getItemsByRequestId(Long itemRequestId) {
        return itemRepository.findAllByRequestId(itemRequestId, SORT_BY_ID_ASC);
    }

    public BookingDtoForItemDto getLastBooking(Long itemId) {
        Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(itemId,
                BookingStatus.APPROVED, LocalDateTime.now());

        Optional<Booking> lastBookingAction = bookingRepository.findByItemIdAndStatusAndStartBeforeAndEndAfter(itemId,
                BookingStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now());

        if (lastBookingAction.isPresent()) {
            return lastBookingAction.map(bookingMapper::toBookingDtoForItemDto).orElse(null);
        } else {
            return lastBooking.map(bookingMapper::toBookingDtoForItemDto).orElse(null);
        }
    }

    public BookingDtoForItemDto getNextBooking(Long itemId) {
        Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(itemId,
                BookingStatus.APPROVED, LocalDateTime.now());
        return nextBooking.map(bookingMapper::toBookingDtoForItemDto).orElse(null);
    }
}
