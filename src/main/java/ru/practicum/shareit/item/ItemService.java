package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.ValidateService;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ValidateService validateService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    Sort sortById = Sort.by(Sort.Direction.ASC, "id");

    public ItemDto create(Long userId, ItemDto itemDto) {
        validateService.checkItemDto(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Item item = mapper.toItem(itemDto);
        item.setOwner(user);
        return mapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) throw new ValidationException("User Id must not be empty");
        if (itemId == null) throw new ValidationException("Item Id must not be empty");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        validateService.checkMatchingIdUsers(userId, item.getOwner().getId());
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        ItemDto finalItemDto = mapper.toItemDto(itemRepository.save(item));
        setLastAndNextBooking(finalItemDto);
        setCommentsInItemDTO(finalItemDto);
        return finalItemDto;
    }

    public void setLastAndNextBooking(ItemDto itemDto) {
        Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(itemDto.getId(),
                BookingStatus.APPROVED, LocalDateTime.now());

        Optional<Booking> lastBookingAction = bookingRepository.findByItemIdAndStatusAndStartBeforeAndEndAfter(itemDto.getId(),
                BookingStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now());

        if (lastBookingAction.isPresent()) {
            itemDto.setLastBooking(lastBookingAction.map(bookingMapper::toBookingDtoNew).orElse(null));
        } else {
            itemDto.setLastBooking(lastBooking.map(bookingMapper::toBookingDtoNew).orElse(null));
        }

        Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(itemDto.getId(),
                BookingStatus.APPROVED, LocalDateTime.now());
        itemDto.setNextBooking(nextBooking.map(bookingMapper::toBookingDtoNew).orElse(null));
    }

    public void setCommentsInItemDTO(ItemDto itemDto) {
        List<CommentDto> comments = commentRepository.findAllByItemId(itemDto.getId()).stream()
                .map(commentMapper::toCommentDto).collect(Collectors.toList());
        itemDto.setComments(comments);
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public ItemDto getById(Long id, Long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
        ItemDto itemDto = mapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            setLastAndNextBooking(itemDto);
        } else {
            itemDto.setNextBooking(null);
        }
        setCommentsInItemDTO(itemDto);
        return itemDto;
    }

    public Collection<ItemDto> getAll(Long userId) {
        return itemRepository.findItemsByOwnerId(userId, sortById).stream()
                .map(mapper::toItemDto)
                .peek(this::setLastAndNextBooking)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        return itemRepository.search(text).stream()
                .map(mapper::toItemDto)
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
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }


}
