package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public OwnerItemDto getById(long itemId, long userId) {
        OwnerItemDto result = ItemMapper.getOwnerDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item")));
        LocalDateTime today = LocalDateTime.now();
        fillByBooking(result, today, userId);
        fillByComments(result);
        log.debug("Отправлен OwnerItemDto {}", result);
        return result;
    }

    @Override
    public Item findById(long itemId) {
        Item result = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item"));
        log.debug("Отправлен Item {}", result);
        return result;
    }

    @Override
    public List<OwnerItemDto> getAll(long userId) {
        userService.getById(userId);
        LocalDateTime today = LocalDateTime.now();
        List<OwnerItemDto> result = itemRepository.findAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .map(ItemMapper::getOwnerDto)
                .map(ownerItemDto -> fillByBooking(ownerItemDto, today, userId))
                .map(this::fillByComments)
                .collect(Collectors.toList());
        log.debug("Отправлен список ItemDto {}", result);
        return result;
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) return new ArrayList<>();
        List<ItemDto> result = itemRepository.searchByText(text);
        log.debug("Отправлен список ItemDto {}", result);
        return result;
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        userService.getById(userId);
        Item item = itemRepository.save(ItemMapper.getModel(itemDto, userId));
        ItemDto result = ItemMapper.getDto(item);
        log.debug("Отправлен ItemDto {}", result);
        return result;
    }

    @Override
    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        LocalDateTime today = LocalDateTime.now();
        bookingRepository.findFirstByItemIdAndBookerIdAndStateAndEndIsBefore(itemId, userId, BookingState.APPROVED, today)
                .orElseThrow(() -> new ValidationException("userId"));
        Comment comment = CommentMapper.getModel(commentDto, itemId);
        comment.setAuthor(userService.findById(userId));
        comment.setCreated(today);
        log.debug("Добавлен Comment {}", comment);
        return CommentMapper.getDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        userService.getById(userId);
        long itemId = itemDto.getId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item"));
        if (item.getOwnerId() == userId) {
            Item updatingItem = ItemMapper.getModel(itemDto, userId);
            updatingItem.setId(itemId);

            if (updatingItem.getName() == null) updatingItem.setName(item.getName());
            if (updatingItem.getDescription() == null) updatingItem.setDescription(item.getDescription());
            if (updatingItem.getAvailable() == null) updatingItem.setAvailable(item.getAvailable());

            ItemDto result = ItemMapper.getDto(itemRepository.save(updatingItem));
            log.debug("Отправлен ItemDto {}", result);
            return result;
        }
        throw new NotFoundException("owner id");
    }

    @Override
    public void deleteById(long itemId, long userId) {
        UserDto userDto = userService.getById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item"));
        if (item.getOwnerId() == userDto.getId()) {
            itemRepository.deleteById(itemId);
            commentRepository.deleteAllByItemId(itemId);
            log.debug("Item с id = {} удален", itemId);
        } else throw new ValidationException("owner id");
    }

    @Override
    public void deleteAll() {
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        log.debug("Все элементы Item удалены");
    }

    private OwnerItemDto fillByBooking(OwnerItemDto ownerItemDto, LocalDateTime today, long userId) {
        if (ownerItemDto.getOwnerId() == userId) {
            long itemId = ownerItemDto.getId();

            Booking nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStateNotOrderByStartAsc(itemId, today, BookingState.REJECTED);
            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStateNotOrderByEndDesc(itemId, today, BookingState.REJECTED);

            if (nextBooking != null) ownerItemDto.setNextBooking(BookingMapper.getNearest(nextBooking));
            if (lastBooking != null) ownerItemDto.setLastBooking(BookingMapper.getNearest(lastBooking));
        }
        return ownerItemDto;
    }

    private OwnerItemDto fillByComments(OwnerItemDto ownerItemDto) {
        ownerItemDto.setComments(commentRepository.findAllByItemId(ownerItemDto.getId()).stream()
                .map(CommentMapper::getDto)
                .collect(Collectors.toSet()));
        return ownerItemDto;
    }
}
