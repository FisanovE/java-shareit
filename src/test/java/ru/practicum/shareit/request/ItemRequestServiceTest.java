package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.common.ValidateService;
import ru.practicum.shareit.common.exeptions.NotFoundException;
import ru.practicum.shareit.common.exeptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoIn;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.Constants.SORT_BY_CREATED_DESC;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ValidateService validateService; // не удалять, для работы void методов ValidateService
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestService itemRequestService;


    @Test
    void create_whenDescriptionNotEmpty_thenReturnDto() {
        Long userId = 0L;
        String description = "blah-blah-blah";
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();
        itemRequestDtoIn.setDescription(description);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto actualItemRequestDto = itemRequestService.create(userId, itemRequestDtoIn);

        assertEquals(itemRequestDto, actualItemRequestDto);
    }

    @Test
    void create_whenUserNotFound_thenReturnException() {
        Long userId = 0L;
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.create(userId, itemRequestDtoIn));
    }

    @Test
    void create_whenDescriptionIsEmpty_thenReturnException() {
        Long userId = 0L;
        ItemRequestDtoIn itemRequestDtoIn = new ItemRequestDtoIn();

        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> itemRequestService.create(userId, itemRequestDtoIn));
    }

    @Test
    void getAllByUser_whenExistsUser_thenReturnDtoList() {
        Long userId = 0L;
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Item item = new Item();
        List<Item> items = List.of(item);
        itemRequestDto.setItems(items);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorId(userId, SORT_BY_CREATED_DESC)).thenReturn(itemRequests);
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemService.getItemsByRequestId(itemRequestDto.getId())).thenReturn(items);

        List<ItemRequestDto> actualItemRequests = itemRequestService.getAllByUser(userId);

        assertEquals(itemRequestDto, actualItemRequests.get(0));
    }

    @Test
    void getAllByUser_whenUserNotFound_thenReturnException() {
        Long userId = 0L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllByUser(userId));
    }

    @Test
    void getAll_whenExistsUser_thenReturnDtoList() {
        Long userId = 0L;
        int from = 1;
        int size = 1;
        ItemRequest itemRequest = new ItemRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Item item = new Item();
        List<Item> items = List.of(item);
        itemRequestDto.setItems(items);
        Pageable sortedByCreated = PageRequest.of(from, size, SORT_BY_CREATED_DESC);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNot(userId, sortedByCreated)).thenReturn(itemRequests);
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemService.getItemsByRequestId(itemRequestDto.getId())).thenReturn(items);

        List<ItemRequestDto> actualItemRequests = (List<ItemRequestDto>) itemRequestService.getAll(userId, from, size);

        assertEquals(itemRequestDto, actualItemRequests.get(0));
    }

    @Test
    void getAll_whenUserNotFound_thenReturnException() {
        Long userId = 0L;
        int from = 1;
        int size = 1;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getAll(userId, from, size));
    }

    @Test
    void getById_whenExistsUser_thenReturnDtoList() {
        Long userId = 0L;
        Long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestorId(userId);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Item item = new Item();
        List<Item> items = List.of(item);
        itemRequestDto.setId(requestId);
        itemRequestDto.setItems(items);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDto);
        when(itemService.getItemsByRequestId(requestId)).thenReturn(items);

        ItemRequestDto actualItemRequest = itemRequestService.getById(requestId, userId);

        assertEquals(itemRequestDto, actualItemRequest);
    }

    @Test
    void getById_whenRequestNotFound_thenReturnException() {
        Long userId = 0L;
        Long requestId = 1L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestorId(userId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(requestId, userId));
    }

    @Test
    void getById_whenUserNotFound_thenReturnException() {
        Long userId = 0L;
        Long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(requestId, userId));
    }
}