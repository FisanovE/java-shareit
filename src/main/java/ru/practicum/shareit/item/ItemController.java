package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private static final String headerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader(headerUserId) Long userId) {
        log.info("Create ItemDto");
        return service.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id, @RequestBody ItemDto itemDto,
                          @RequestHeader(headerUserId) Long userId) {
        log.info("Update ItemDto {}", id);
        return service.update(userId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Delete ItemDto {}", id);
        service.delete(id);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable(required = false) Long id) {
        log.info("Get Item {}", id);
        return service.getById(id);
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader(headerUserId) Long userId) {
        log.info("Get ItemDtos");
        return service.getAll(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        log.info("Get Items contains {}", text);
        return service.search(text);
    }
}
