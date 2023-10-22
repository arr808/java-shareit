package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.PaginationAndSortParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemServiceImpl itemService;
    private User owner;
    private User author;
    private Item item;
    private ItemDto itemDto;
    private ItemDto updateItemDto;
    private ItemRequest itemRequest;
    private Comment comment;
    private CommentDto commentDto;
    private final long ownerId = 1;
    private final long authorId = 2;
    private final long itemId = 1;
    private final long commentId = 1;
    private final long itemRequestId = 1;
    private final LocalDateTime timestamp = LocalDateTime.now().plusMinutes(1);
    private final String newName = "new name";
    private final String newDescription = "new desc";

    @BeforeEach
    public void createEntity() {
        owner = User.builder()
                .id(ownerId)
                .name("owner")
                .email("owner@email.ru")
                .build();

        author = User.builder()
                .id(authorId)
                .name("booker")
                .email("booker@email.ru")
                .build();

        item = Item.builder()
                .id(itemId)
                .name("item")
                .description("item desc")
                .owner(owner)
                .available(true)
                .build();

        comment = Comment.builder()
                .id(commentId)
                .text("text")
                .item(item)
                .author(author)
                .created(timestamp)
                .build();

        commentDto = CommentDto.builder()
                .id(commentId)
                .text("text")
                .authorName(author.getName())
                .created(timestamp)
                .build();

        itemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("item desc")
                .available(true)
                .build();

        updateItemDto = ItemDto.builder()
                .id(itemId)
                .name("item")
                .description("item desc")
                .available(true)
                .build();

        itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description("item")
                .requester(author)
                .creation(timestamp)
                .build();
    }

    @Test
    public void shouldReturnItemById() {
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));
        when(commentRepository.findAllByItemIdOrderByCreated(itemId))
                .thenReturn(List.of(comment));

        Assertions.assertEquals(itemDto, itemService.getById(itemId, ownerId));
    }

    @Test
    public void shouldThrowExceptionGetItemByIdWhenUnknownItem() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getById(99, ownerId));

        Assertions.assertEquals("item", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldReturnItems() {
        Pageable pageRequest = PaginationAndSortParams.getPageable(0, 1);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findAllByOwnerId(ownerId, pageRequest))
                .thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdOrderByCreated(itemId))
                .thenReturn(List.of(comment));

        Assertions.assertEquals(List.of(itemDto), itemService.getAll(ownerId, 0, 1));
    }

    @Test
    public void shouldThrowExceptionGetAllWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getAll(99, 0, 1));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldReturnSearchedItems() {
        Pageable pageRequest = PaginationAndSortParams.getPageable(0, 1);

        when(itemRepository.searchByText("text", pageRequest))
                .thenReturn(List.of(item));

        Assertions.assertEquals(List.of(itemDto), itemService.searchByText("text", 0, 1));
    }

    @Test
    public void shouldReturnEmptyListWhenTextBlank() {
        Assertions.assertEquals(List.of(), itemService.searchByText("", 0, 1));
    }

    @Test
    public void shouldAddNewItem() {
        ItemDto noIdItemDto = ItemDto.builder()
                .name("item")
                .description("item desc")
                .available(true)
                .build();

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Assertions.assertEquals(itemDto, itemService.add(noIdItemDto, ownerId));
    }

    @Test
    public void shouldThrowExceptionAddNewItemWhenUnknownUser() {
        ItemDto noIdItemDto = ItemDto.builder()
                .name("item")
                .description("item desc")
                .available(true)
                .build();

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.add(noIdItemDto, 99));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldAddNewItemForRequest() {
        ItemDto noIdItemDto = ItemDto.builder()
                .name("item")
                .description("item desc")
                .available(true)
                .requestId(itemRequestId)
                .build();
        itemDto.setRequestId(itemRequestId);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRequestRepository.findById(itemRequestId))
                .thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Assertions.assertEquals(itemDto, itemService.add(noIdItemDto, ownerId));
    }

    @Test
    public void shouldAddNewComment() {
        CommentDto noIdCommentDto = CommentDto.builder()
                .text("text")
                .authorName(author.getName())
                .created(timestamp)
                .build();

        when(bookingRepository.findFirstByItemIdAndBookerIdAndStateAndEndIsBefore(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.ofNullable(new Booking()));
        when(userRepository.findById(authorId))
                .thenReturn(Optional.ofNullable(author));
        when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);

        Assertions.assertEquals(commentDto, itemService.addComment(itemId, authorId, noIdCommentDto));
    }

    @Test
    public void shouldThrowExceptionAddNewCommentWhenIncorrectUser() {
        CommentDto noIdCommentDto = CommentDto.builder()
                .text("text")
                .authorName(author.getName())
                .created(timestamp)
                .build();

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemService.addComment(itemId, ownerId, noIdCommentDto));

        Assertions.assertEquals("userId", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddNewCommentWhenUnknownUser() {
        CommentDto noIdCommentDto = CommentDto.builder()
                .text("text")
                .authorName(author.getName())
                .created(timestamp)
                .build();

        when(bookingRepository.findFirstByItemIdAndBookerIdAndStateAndEndIsBefore(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.ofNullable(new Booking()));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.addComment(itemId, 99, noIdCommentDto));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldUpdateItemName() {
        updateItemDto.setName(newName);
        itemDto.setName(newName);
        item.setName(newName);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Assertions.assertEquals(itemDto, itemService.update(updateItemDto, ownerId));
    }

    @Test
    public void shouldUpdateItemDescription() {
        updateItemDto.setDescription(newDescription);
        itemDto.setDescription(newDescription);
        item.setDescription(newDescription);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Assertions.assertEquals(itemDto, itemService.update(updateItemDto, ownerId));
    }

    @Test
    public void shouldUpdateItemAvailable() {
        updateItemDto.setAvailable(false);
        itemDto.setAvailable(false);
        item.setAvailable(false);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        Assertions.assertEquals(itemDto, itemService.update(updateItemDto, ownerId));
    }

    @Test
    public void shouldThrowExceptionUpdateWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(updateItemDto, 99));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionUpdateWhenUnknownItem() {
        updateItemDto.setId(99);

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(updateItemDto, ownerId));

        Assertions.assertEquals("item", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionUpdateWhenIncorrectOwnerId() {
        when(userRepository.findById(authorId))
                .thenReturn(Optional.ofNullable(author));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(updateItemDto, authorId));

        Assertions.assertEquals("owner id", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldDeleteItemById() {
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        itemService.deleteById(itemId, ownerId);

        verify(itemRepository, Mockito.times(1))
                .deleteById(itemId);
        verify(commentRepository, Mockito.times(1))
                .deleteAllByItemId(itemId);
    }

    @Test
    public void shouldThrowExceptionDeleteItemByIdWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.deleteById(itemId, 99));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionDeleteItemByIdWhenUnknownItem() {
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.deleteById(99, ownerId));

        Assertions.assertEquals("item", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionDeleteItemByIdWhenWrongUser() {
        when(userRepository.findById(authorId))
                .thenReturn(Optional.ofNullable(author));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemService.deleteById(itemId, authorId));

        Assertions.assertEquals("owner id", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldDeleteAll() {
        itemService.deleteAll();

        verify(itemRepository, Mockito.times(1))
                .deleteAll();
        verify(commentRepository, Mockito.times(1))
                .deleteAll();
    }
}
