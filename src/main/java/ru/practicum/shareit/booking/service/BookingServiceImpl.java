package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.controller.RequestState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        return null;
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(long bookerId, RequestState state) {
        return null;
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(long ownerId, RequestState state) {
        return null;
    }

    @Override
    public BookingDto addBooking(Booking booking, long bookerId) {
        return null;
    }

    @Override
    public BookingDto bookingReview(long bookingId, long ownerId, boolean approved) {
        return null;
    }
}
