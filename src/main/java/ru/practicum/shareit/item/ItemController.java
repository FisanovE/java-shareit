package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.*;

import java.util.Collection;

import static ru.practicum.shareit.common.Constants.HEADER_USER_ID;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Create ItemDto");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Update ItemDto {}", id);
        return itemService.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Delete ItemDto {}", id);
        itemService.delete(id);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable(required = false) Long id,
                           @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Get Item {}", id);
        return itemService.getById(id, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @RequestParam(defaultValue = "0", required = false) Integer from,
                                      @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get All ItemDto of user {} from {} size {}", userId, from, size);
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text,
                                      @RequestParam(defaultValue = "0", required = false) Integer from,
                                      @RequestParam(defaultValue = "10", required = false) Integer size) {
        log.info("Get ItemsDto contains {} from {} size {}", text, from, size);
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable(required = false) Long itemId,
                                    @RequestBody Comment comment,
                                    @RequestHeader(HEADER_USER_ID) Long userId) {
        log.info("Create comment User {} item {}", userId, itemId);
        return itemService.createComment(userId, itemId, comment);
    }
}
