package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.common.Constants.SORT_BY_ID_ASC;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    RequestRepository requestRepository;

    Long requestorId;
    Long ownerId;
    Long itemId;
    Long itemRequestId;
    ItemRequest itemRequest;
    Sort sort;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        User requestor = userRepository.save(new User(null, "requestor", "booker@mail.ru"));
        requestorId = requestor.getId();
        User owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));
        ownerId = owner.getId();
        itemRequest = requestRepository.save(new ItemRequest(null, "description_requestor", requestorId,
                LocalDateTime.now(), null));
        itemRequestId = itemRequest.getId();
        Item item = itemRepository.save(new Item(null, "Дрель", "description", true,
                owner, itemRequestId));
        itemId = item.getId();
        List<Item> items = List.of(item);

        int from = 0;
        int size = 1;
        sort = SORT_BY_ID_ASC;
        pageable = PageRequest.of(from, size, sort);
    }

    @Test
    void search() {
        String text = "дрЕЛь";
        List<Item> actualItems = itemRepository.search(text, pageable);

        assertFalse(actualItems.isEmpty());
    }

    @Test
    void findItemsByOwnerId() {
        List<Item> actualItems = itemRepository.findItemsByOwnerId(ownerId, pageable);

        assertFalse(actualItems.isEmpty());
    }

    @Test
    void findAllByRequestId() {
        List<Item> actualItems = itemRepository.findAllByRequestId(itemRequestId, sort);

        assertFalse(actualItems.isEmpty());
    }

    @AfterEach
    void deleteAll() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}