package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.*;

import java.util.Collection;

import static ru.practicum.shareit.common.Constants.headerUserId;

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
                          @RequestHeader(headerUserId) Long userId) {
        log.info("Create ItemDto");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(headerUserId) Long userId) {
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
                           @RequestHeader(headerUserId) Long userId) {
        log.info("Get Item {}", id);
        return itemService.getById(id, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader(headerUserId) Long userId) {
        log.info("Get ItemDtos");
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.info("Get Items contains {}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable(required = false) Long itemId,
                                    @RequestBody Comment comment,
                                    @RequestHeader(headerUserId) Long userId) {
        log.info("Create comment User {} item {}", userId, itemId);
        return itemService.createComment(userId, itemId, comment);
    }
}
