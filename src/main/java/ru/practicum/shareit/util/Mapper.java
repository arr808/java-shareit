package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class Mapper {

    //User
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User fromDto(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    //Item
    public static ItemDto toDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .build();
        if (item.getItemRequest() != null) itemDto.setRequestId(item.getItemRequest().getId());
        return itemDto;
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest().getId())
                .build();
    }

    public static Item fromDto(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(owner)
                .available(itemDto.getAvailable())
                .build();
    }

    //Comment
    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment fromDto(CommentDto commentDto, long itemId) {
        return Comment.builder()
                .item(Item.builder().id(itemId).build())
                .text(commentDto.getText())
                .build();
    }

    //Booking
    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(Mapper.toDto(booking.getItem()))
                .booker(Mapper.toDto(booking.getBooker()))
                .status(booking.getState())
                .build();
    }

    public static BookingDtoShort toShortDto(Booking booking) {
        return BookingDtoShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Booking fromRequestDto(BookingRequestDto bookingRequestDto, User booker, Item item) {
        return Booking.builder()
                .start((bookingRequestDto.getStart()))
                .end(bookingRequestDto.getEnd())
                .booker(booker)
                .item(item)
                .state(BookingStatus.WAITING)
                .build();
    }

    //ItemRequest
    public static ItemRequestDto toDto (ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreation())
                .build();
    }

    public static ItemRequest fromShortDto(User requester, ItemRequestShortDto shortDto, LocalDateTime timestamp) {
        return ItemRequest.builder()
                .description(shortDto.getDescription())
                .requester(requester)
                .creation(timestamp)
                .build();
    }
}
