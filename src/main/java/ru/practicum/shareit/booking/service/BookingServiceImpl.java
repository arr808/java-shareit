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
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
        userService.getById(userId);
        return BookingMapper.getDto(bookingRepository.getBookingById(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("booking")));
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(long bookerId, RequestState state) {
        userService.getById(bookerId);
        LocalDateTime today = LocalDateTime.now();

        switch (state.toString()) {
            case ("ALL"):
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("WAITING"):
                return bookingRepository.findAllByBookerIdAndStateOrderByStartDesc(bookerId, BookingState.WAITING).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("REJECTED"):
                return bookingRepository.findAllByBookerIdAndStateOrderByStartDesc(bookerId, BookingState.REJECTED).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("CURRENT"):
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId, today, today).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("PAST"):
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByStartDesc(bookerId, today, today).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("FUTURE"):
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, today).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(long ownerId, RequestState state) {
        userService.getById(ownerId);
        LocalDateTime today = LocalDateTime.now();

        switch (state.toString()) {
            case ("ALL"):
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("WAITING"):
                return bookingRepository.findAllByItemOwnerIdAndStateOrderByStartDesc(ownerId, BookingState.WAITING).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("REJECTED"):
                return bookingRepository.findAllByItemOwnerIdAndStateOrderByStartDesc(ownerId, BookingState.REJECTED).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("CURRENT"):
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, today, today)
                        .stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("PAST"):
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByStartDesc(ownerId, today, today)
                        .stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            case ("FUTURE"):
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, today).stream()
                        .map(BookingMapper::getDto)
                        .collect(Collectors.toList());
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public BookingDto addBooking(BookingRequestDto bookingRequestDto, long bookerId) {
        User booker = UserMapper.getModel(userService.getById(bookerId));
        Item item = itemService.findById(bookingRequestDto.getItemId());

        validation(bookingRequestDto, item, bookerId);

        Booking booking = BookingMapper.getModel(bookingRequestDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setState(BookingState.WAITING);
        return BookingMapper.getDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto bookingReview(long bookingId, long ownerId, boolean approved) {
        userService.getById(ownerId);
        Booking booking = bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new NotFoundException("booking"));

        if (booking.getState().equals(BookingState.APPROVED)) throw new ValidationException("approved");

        if (approved) booking.setState(BookingState.APPROVED);
        else booking.setState(BookingState.REJECTED);

        return BookingMapper.getDto(bookingRepository.save(booking));
    }

    private void validation(BookingRequestDto bookingRequestDto, Item item, long bookerId) {
        if (bookerId == item.getOwnerId()) throw new NotFoundException("owner");

        boolean isAvailable = item.getAvailable();
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();

        if (!isAvailable) throw new AlreadyBusyException("booking");

        if (start == null
                || end == null
                || end.isBefore(start)
                || end.isBefore(LocalDateTime.now())
                || start.isEqual(end)
                || start.isBefore(LocalDateTime.now())) throw new ValidationException("time");
    }
}
