package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@Controller
@RequestMapping(path = "/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(HEADER_USER_ID) long userId,
                                         @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        log.info("Create ItemRequest user {} ", userId);
        return itemRequestClient.create(userId, itemRequestDtoIn);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Get All ItemRequest user {}", userId);
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER_USER_ID) long userId,
                                          @PathVariable(required = false) long requestId) {
        log.info("Get ItemRequest {} user {} ", requestId, userId);
        return itemRequestClient.getById(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER_USER_ID) long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get All ItemRequests of user {} from {} size {}", userId, from, size);
        return itemRequestClient.getAll(userId, from, size);
    }
}
