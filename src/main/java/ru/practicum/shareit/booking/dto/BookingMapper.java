package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

@UtilityClass
public class BookingMapper {

    public static BookingDto getDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.getDto(booking.getItem()))
                .booker(UserMapper.getDto(booking.getBooker()))
                .status(booking.getState())
                .build();
    }

    public static Booking getModel(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .start((bookingRequestDto.getStart()))
                .end(bookingRequestDto.getEnd())
                .build();
    }

    public static NearestBooking getNearest(Booking booking) {
        return NearestBooking.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
