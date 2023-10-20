package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.PaginationAndSortParams;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestShortDto itemRequestShortDto;
    private User requester;
    private final long itemRequestId = 1;
    private final long requesterId = 1;
    private final LocalDateTime timestamp = LocalDateTime.now().plusMinutes(1);

    @BeforeEach
    public void createEntity() {
        requester = User.builder()
                .id(requesterId)
                .name("requester")
                .email("requester@email.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description("desc")
                .requester(requester)
                .creation(timestamp)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(itemRequestId)
                .description("desc")
                .created(timestamp)
                .items(new ArrayList<>())
                .build();

        itemRequestShortDto = ItemRequestShortDto.builder()
                .description("desc")
                .creation(timestamp)
                .build();
    }

    @Test
    public void shouldReturnAllByUser() {
        when(userRepository.findById(requesterId))
                .thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreationDesc(requesterId))
                .thenReturn(List.of(itemRequest));

        Assertions.assertEquals(List.of(itemRequestDto), itemRequestService.getAllByUser(requesterId));
    }

    @Test
    public void shouldThrowExceptionReturnAllByUserWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllByUser(99));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldReturnAll() {
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(0, 1, "creation");

        when(itemRequestRepository.findAllByRequesterIdNot(requesterId, pageRequest))
                .thenReturn(List.of(itemRequest));

        Assertions.assertEquals(List.of(itemRequestDto), itemRequestService.getAll(requesterId, 0, 1));
    }

    @Test
    public void shouldReturnById() {
        when(userRepository.findById(requesterId))
                .thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.ofNullable(itemRequest));

        Assertions.assertEquals(itemRequestDto, itemRequestService.getById(requesterId, itemRequestId));
    }

    @Test
    public void shouldThrowExceptionGetByIdWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(99, itemRequestId));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionGetByIdWhenUnknownItemRequest() {
        when(userRepository.findById(requesterId))
                .thenReturn(Optional.ofNullable(requester));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(requesterId, 99));

        Assertions.assertEquals("request", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldAddRequest() {
        itemRequestDto.setItems(null);
        when(userRepository.findById(requesterId))
                .thenReturn(Optional.ofNullable(requester));
        when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);

        Assertions.assertEquals(itemRequestDto, itemRequestService.addRequest(requesterId, itemRequestShortDto));
    }

    @Test
    public void shouldThrowExceptionAddRequestWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.addRequest(99, itemRequestShortDto));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

}
