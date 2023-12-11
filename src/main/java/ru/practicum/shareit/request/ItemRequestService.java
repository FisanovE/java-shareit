package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.common.Constants.SORT_BY_CREATED_DESC;
import static ru.practicum.shareit.common.Constants.SORT_BY_ID_ASC;

@RequiredArgsConstructor
@Service
public class ItemRequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    public ItemRequestDto create(Long userId, ItemRequestDtoIn itemRequestDtoIn) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        if (itemRequestDtoIn.getDescription() == null || itemRequestDtoIn.getDescription().isBlank()) {
            throw new ValidationException("Description must not be empty");
        }
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDtoIn);
        itemRequest.setRequestorId(userId);
        return itemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> getAllByUser(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        return requestRepository.findAllByRequestorId(userId, SORT_BY_CREATED_DESC).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId(),
                        SORT_BY_ID_ASC)))
                .collect(Collectors.toList());
    }

    public Collection<ItemRequestDto> getAll(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        Pageable sortedByCreated = PageRequest.of(from, size, SORT_BY_CREATED_DESC);
        return requestRepository.findAllByRequestorIdNot(userId, sortedByCreated).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .peek(itemRequestDto -> itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId(),
                        SORT_BY_ID_ASC)))
                .collect(Collectors.toList());
    }

    public ItemRequestDto getById(Long requestId, Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found: " + requestId));
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findAllByRequestId(itemRequestDto.getId(), SORT_BY_ID_ASC));
        return itemRequestDto;
    }
}