package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.controller.RequestState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByBooker(long bookerId, RequestState state);

    List<BookingDto> getAllBookingsByOwner(long ownerId, RequestState state);

    BookingDto addBooking(BookingRequestDto bookingRequestDto, long bookerId);

    BookingDto bookingReview(long bookingId, long ownerId, boolean approved);
}
