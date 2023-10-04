package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.controller.RequestState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AlreadyBusyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        return BookingMapper.getDto(bookingRepository.getBookingById(bookingId, userId)
                .orElseThrow(() -> new ValidationException("booking")));
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
    public BookingDto addBooking(BookingRequestDto bookingRequestDto, long bookerId) {
        User booker = UserMapper.getModel(userService.getById(bookerId));
        Item item = itemService.findById(bookingRequestDto.getItemId());

        validation(bookingRequestDto, item);

        Booking booking = BookingMapper.getModel(bookingRequestDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setState(BookingState.WAITING);
        return BookingMapper.getDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto bookingReview(long bookingId, long ownerId, boolean approved) {
        userService.getById(ownerId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("booking"));
        if (approved) booking.setState(BookingState.APPROVED);
        else booking.setState(BookingState.REJECTED);
        return BookingMapper.getDto(bookingRepository.save(booking));
    }

    private void validation(BookingRequestDto bookingRequestDto, Item item) {
        boolean isAvailable = item.getAvailable();
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();

        if (start == null) throw new ValidationException("start");

        if (end == null) throw new ValidationException("end");

        if (!isAvailable) throw new AlreadyBusyException("booking");

        if (end.isBefore(start) || end.isBefore(LocalDateTime.now())) throw new ValidationException("end");

        if (start.isEqual(end)) throw new ValidationException("start, end");

        if (start.isBefore(LocalDateTime.now())) throw new ValidationException("start");
    }
}
