package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByBooker(long bookerId, BookingState state, int from, int size);

    List<BookingDto> getAllBookingsByOwner(long ownerId, BookingState state, int from, int size);

    BookingDto addBooking(BookingRequestDto bookingRequestDto, long bookerId);

    BookingDto setBookingApprove(long bookingId, long ownerId, boolean approved);
}
