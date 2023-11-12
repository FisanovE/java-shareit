package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.ValidateService;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ValidateService validateService;

    private final ItemMapper mapper;

    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
        validateService.checkItemDto(itemDto);
        Item item = mapper.toItem(itemDto);
        item.setOwner(userId);

        return mapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) throw new ValidationException("User Id must not be empty");
        if (itemId == null) throw new ValidationException("Item Id must not be empty");
        validateService.checkMatchingIdUsers(userId, getById(itemId).getOwner());
        //User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
        userRepository.existsById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        if (itemDto.getOwner() != null) item.setOwner(itemDto.getOwner());
        if (itemDto.getRequest() != null) item.setRequest(itemDto.getRequest());

        return mapper.toItemDto(itemRepository.save(item));
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    public ItemDto getById(Long id) {
        return itemRepository.findById(id)
                .map(mapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
    }

    public Collection<ItemDto> getAll(Long userId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemDto> search(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        return itemRepository.search(text).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }
}
