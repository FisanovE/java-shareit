package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

@Controller
@RequestMapping("/items")
@Validated
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid ItemDto itemDto,
                                         @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Create ItemDto");
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Update ItemDto {}", id);
        return itemClient.update(id, userId, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Delete ItemDto {}", id);
        itemClient.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable(required = false) long id,
                                          @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Get Item {}", id);
        return itemClient.getById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER_USER_ID) long userId,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get All ItemDto of user {} from {} size {}", userId, from, size);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get ItemsDto contains {} from {} size {}", text, from, size);
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable(required = false) long itemId,
                                                @RequestBody Comment comment,
                                                @RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Create comment User {} item {}", userId, itemId);
        return itemClient.createComment(itemId, userId, comment);
    }
}
