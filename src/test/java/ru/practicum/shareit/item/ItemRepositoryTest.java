package ru.practicum.shareit.item;

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

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class ItemRepositoryTest {

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
        userRepository.save(requester);
        item = itemRepository.save(item);
        itemRequest = itemRequestRepository.save(itemRequest);
        item.setItemRequest(itemRequest);
        itemRepository.save(item);
    }

    @Test
    public void shouldSearchByText() {
        List<Item> items = itemRepository.searchByText("name", pageRequest);

        Assertions.assertEquals(List.of(item), items);
    }

    @Test
    public void shouldFindAllByOwnerId() {
        List<Item> items = itemRepository.findAllByOwnerId(owner.getId(), pageRequest);

        Assertions.assertEquals(List.of(item), items);
    }

    @Test
    public void shouldFindAllByRequestId() {
        List<Item> items = itemRepository.findAllByItemRequestId(itemRequest.getId());

        Assertions.assertEquals(List.of(item), items);
    }

    @Test
    public void shouldNotAddItemNoName() {
        Item item2 = Item.builder()
                .description("desc")
                .owner(owner)
                .available(true)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemRepository.save(item2));
    }

    @Test
    public void shouldNotAddItemNoDescription() {
        Item item2 = Item.builder()
                .name("name2")
                .owner(owner)
                .available(true)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemRepository.save(item2));
    }

    @Test
    public void shouldNotAddItemNoOwner() {
        Item item2 = Item.builder()
                .name("name2")
                .description("desc")
                .available(true)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemRepository.save(item2));
    }

    @Test
    public void shouldNotAddItemNoAvailable() {
        Item item2 = Item.builder()
                .name("name2")
                .description("desc")
                .owner(owner)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> itemRepository.save(item2));
    }
}
