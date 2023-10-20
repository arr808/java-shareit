package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.PaginationAndSortParams;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Item item;
    private User owner;
    private User requester;
    private ItemRequest itemRequest;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final Pageable pageRequest = PaginationAndSortParams.getPageable(0, 1);


    @BeforeEach
    public void createEntity() {
        owner = User.builder()
                .name("owner")
                .email("owner@email.ru")
                .build();

        requester = User.builder()
                .name("requester")
                .email("requester@email.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .description("desc")
                .requester(requester)
                .creation(timestamp)
                .build();

        item = Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build();

        userRepository.save(owner);
        requester = userRepository.save(requester);
        item = itemRepository.save(item);
        itemRequest = itemRequestRepository.save(itemRequest);
        item.setItemRequest(itemRequest);
        itemRepository.save(item);
    }

    @Test
    public void shouldFindAllByRequesterId() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreationDesc(requester.getId());

        Assertions.assertEquals(List.of(itemRequest), itemRequests);
    }

    @Test
    public void shouldFindAllById() {
        ItemRequest request = itemRequestRepository.findById(itemRequest.getId()).get();

        Assertions.assertEquals(itemRequest, request);
    }

    @Test
    public void shouldFindAllByRequesterIdNot() {
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(requester.getId(), pageRequest);

        Assertions.assertEquals(0, itemRequests.size());
    }

    @Test
    public void shouldNotAddItemRequestNoDescription() {
        ItemRequest itemRequest2 = ItemRequest.builder()
                .requester(requester)
                .creation(timestamp)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> itemRequestRepository.save(itemRequest2));
    }

    @Test
    public void shouldNotAddItemRequestNoRequester() {
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("desc")
                .creation(timestamp)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemRequestRepository.save(itemRequest2));
    }

    @Test
    public void shouldNotAddItemRequestNoCreation() {
        ItemRequest itemRequest2 = ItemRequest.builder()
                .description("desc")
                .requester(requester)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemRequestRepository.save(itemRequest2));
    }
}
