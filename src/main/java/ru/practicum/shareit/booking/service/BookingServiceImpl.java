package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        BookingDto result = BookingMapper.getDto(bookingRepository.getBookingById(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("booking")));
        log.debug("Отправлен BookingDto {}", result);
        return result;
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(long bookerId, RequestState state) {
        userService.getById(bookerId);

        LocalDateTime today = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toString()) {
            case ("ALL"):
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
            case ("WAITING"):
                bookings = bookingRepository.findAllByBookerIdAndStateOrderByStartDesc(bookerId, BookingState.WAITING);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findAllByBookerIdAndStateOrderByStartDesc(bookerId, BookingState.REJECTED);
                break;
            case ("CURRENT"):
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId, today, today);
                break;
            case ("PAST"):
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByStartDesc(bookerId, today, today);
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, today);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.debug("Отправлен список Booking {}", bookings);
        return bookings.stream()
                .map(BookingMapper::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(long ownerId, RequestState state) {
        userService.getById(ownerId);

        LocalDateTime today = LocalDateTime.now();
        List<Booking> bookings;

        switch (state.toString()) {
            case ("ALL"):
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case ("WAITING"):
                bookings = bookingRepository.findAllByItemOwnerIdAndStateOrderByStartDesc(ownerId, BookingState.WAITING);
                break;
            case ("REJECTED"):
                bookings = bookingRepository.findAllByItemOwnerIdAndStateOrderByStartDesc(ownerId, BookingState.REJECTED);
                break;
            case ("CURRENT"):
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId, today, today);
                break;
            case ("PAST"):
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByStartDesc(ownerId, today, today);
                break;
            case ("FUTURE"):
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(ownerId, today);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.debug("Отправлен список Booking {}", bookings);
        return bookings.stream()
                .map(BookingMapper::getDto)
                .collect(Collectors.toList());
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
        log.debug("Добавлен новый Booking {}", booking);
        return BookingMapper.getDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto bookingReview(long bookingId, long ownerId, boolean approved) {
        userService.getById(ownerId);
        Booking booking = bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new NotFoundException("booking"));

        if (booking.getState().equals(BookingState.APPROVED)) throw new ValidationException("approved");

        if (approved) {
            booking.setState(BookingState.APPROVED);
        } else booking.setState(BookingState.REJECTED);
        log.debug("Статус Booking {} обновлен", booking);
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
