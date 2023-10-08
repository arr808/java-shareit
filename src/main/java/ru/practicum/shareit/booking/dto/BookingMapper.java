package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toDto(booking.getItem()))
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getState())
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

    public static BookingDtoShort toShortDto(Booking booking) {
        return BookingDtoShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
