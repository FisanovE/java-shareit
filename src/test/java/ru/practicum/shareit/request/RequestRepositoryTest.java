package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.common.Constants.SORT_BY_CREATED_DESC;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    RequestRepository requestRepository;

    Long requestorId;
    Long ownerId;
    ItemRequest itemRequestFromOwner;
    ItemRequest itemRequestFromRequestor;
    Sort sort;
    Pageable pageable ;


    @BeforeEach
    void setUp() {
        User requestor = userRepository.save(new User(null, "requestor", "booker@mail.ru"));
        requestorId = requestor.getId();
        User owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));
        ownerId = owner.getId();
        Item item = itemRepository.save(new Item(null, "Name", "description", true,
                owner, null));
        List<Item> items = List.of(item);
        itemRequestFromRequestor = new ItemRequest(null, "description_requestor", requestorId,
                LocalDateTime.now(), items);
        itemRequestFromOwner = new ItemRequest(null, "description_owner", ownerId,
                LocalDateTime.now(), items);
        requestRepository.save(itemRequestFromRequestor);
        requestRepository.save(itemRequestFromOwner);
        int from = 0;
        int size = 1;
        sort = SORT_BY_CREATED_DESC;
        pageable = PageRequest.of(from, size, sort);
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> actualItemRequest = requestRepository.findAllByRequestorId(requestorId, sort);

        assertEquals(1, actualItemRequest.size());
        assertEquals(actualItemRequest.get(0).getRequestorId(), requestorId);
    }

    @Test
    void findAllByRequestorIdNot() {
        List<ItemRequest> actualItemRequest = requestRepository.findAllByRequestorIdNot(requestorId, pageable);

        assertEquals(1, actualItemRequest.size());
        assertEquals(actualItemRequest.get(0).getRequestorId(), ownerId);
    }

    @AfterEach
    void deleteAll() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}