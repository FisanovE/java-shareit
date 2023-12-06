package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoIn;

import java.util.Collection;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(HEADER_USER_ID) Long userId,
                              @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        log.info("Create ItemRequest user {} ", userId);
        return itemRequestService.create(userId, itemRequestDtoIn);
    }

    @GetMapping
    public Collection<ItemRequestDto> getAllByUser(@RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Get All ItemRequest user {}", userId);
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAll(@RequestHeader(HEADER_USER_ID) Long userId,
                                          @RequestParam(defaultValue = "0", required = false) Integer from,
                                          @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get All ItemRequests of user {} from {} size {}", userId, from, size);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(HEADER_USER_ID) Long userId,
                               @PathVariable(required = false) Long requestId) {
        log.info("Get ItemRequest {} user {} ", requestId, userId);
        return itemRequestService.getById(requestId, userId);
    }
}
