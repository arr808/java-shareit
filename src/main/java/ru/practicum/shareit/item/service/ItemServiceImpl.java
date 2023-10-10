package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto getById(long itemId, long userId) {
        ItemDto result = Mapper.toDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item")));
        LocalDateTime today = LocalDateTime.now();
        fillByBooking(result, today, userId);
        fillByComments(result, userId);
        log.debug("Отправлен ItemDto {}", result);
        return result;
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        checkUser(userId);
        LocalDateTime today = LocalDateTime.now();
        List<ItemDto> result = itemRepository.findAllByOwnerId(userId).stream()
                .map(Mapper::toDto)
                .peek(itemDto -> {
                    fillByBooking(itemDto, today, userId);
                    fillByComments(itemDto, userId);
                })
                .collect(Collectors.toList());
        log.debug("Отправлен список ItemDto {}", result);
        return result;
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text.isBlank()) return new ArrayList<>();
        List<ItemDto> result = itemRepository.searchByText(text).stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
        log.debug("Отправлен список ItemDto {}", result);
        return result;
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        User user = checkUser(userId);
        Item item = itemRepository.save(Mapper.fromDto(itemDto, user));
        ItemDto result = Mapper.toDto(item);
        log.debug("Отправлен ItemDto {}", result);
        return result;
    }

    @Override
    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        LocalDateTime today = LocalDateTime.now();
        bookingRepository.findFirstByItemIdAndBookerIdAndStateAndEndIsBefore(itemId, userId, BookingStatus.APPROVED, today)
                .orElseThrow(() -> new ValidationException("userId"));
        Comment comment = Mapper.fromDto(commentDto, itemId);
        comment.setAuthor(checkUser(userId));
        comment.setCreated(today);
        log.debug("Добавлен Comment {}", comment);
        return Mapper.toDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId) {
        User user = checkUser(userId);
        long itemId = itemDto.getId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item"));
        if (item.getOwner().getId() == userId) {
            Item updatingItem = Mapper.fromDto(itemDto, user);
            updatingItem.setId(itemId);

            if (updatingItem.getName() == null) updatingItem.setName(item.getName());
            if (updatingItem.getDescription() == null) updatingItem.setDescription(item.getDescription());
            if (updatingItem.getAvailable() == null) updatingItem.setAvailable(item.getAvailable());

            ItemDto result = Mapper.toDto(itemRepository.save(updatingItem));
            log.debug("Отправлен ItemDto {}", result);
            return result;
        }
        throw new NotFoundException("owner id");
    }

    @Override
    public void deleteById(long itemId, long userId) {
        UserDto userDto = Mapper.toDto(checkUser(userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item"));
        if (item.getOwner().getId() == userDto.getId()) {
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

    private void fillByBooking(ItemDto itemDto, LocalDateTime today, long userId) {
        if (itemDto.getOwnerId() == userId) {
            long itemId = itemDto.getId();

            Booking nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStateNotOrderByStartAsc(itemId, today, BookingStatus.REJECTED);
            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStateNotOrderByEndDesc(itemId, today, BookingStatus.REJECTED);

            if (nextBooking != null) itemDto.setNextBooking(Mapper.toShortDto(nextBooking));
            if (lastBooking != null) itemDto.setLastBooking(Mapper.toShortDto(lastBooking));
        }
    }

    private void fillByComments(ItemDto itemDto, long userId) {
        long itemId = itemDto.getId();
        List<CommentDto> commentsDto = commentRepository.findAllByItemIdOrderByCreated(itemId).stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
        itemDto.setComments(commentsDto);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user"));
    }
}
