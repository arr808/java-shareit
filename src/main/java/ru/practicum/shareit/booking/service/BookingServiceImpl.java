package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.controller.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AlreadyBusyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;
import ru.practicum.shareit.util.PaginationAndSortParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        checkUser(userId);
        BookingDto result = Mapper.toDto(bookingRepository.getBookingById(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("booking")));
        log.debug("Отправлен BookingDto {}", result);
        return result;
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(long bookerId, BookingState state, int from, int size) {
        checkUser(bookerId);
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(from, size, "start");
        LocalDateTime timestamp = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(bookerId, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndState(bookerId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndState(bookerId, BookingStatus.REJECTED, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(bookerId, timestamp, timestamp, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsBefore(bookerId, timestamp, timestamp, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(bookerId, timestamp, pageRequest);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.debug("Отправлен список Booking {}", bookings);
        return bookings.stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(long ownerId, BookingState state, int from, int size) {
        checkUser(ownerId);
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(from, size, "start");
        LocalDateTime timestamp = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerId(ownerId, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndState(ownerId, BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndState(ownerId, BookingStatus.REJECTED, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ownerId, timestamp, timestamp, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsBefore(ownerId, timestamp, timestamp, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(ownerId, timestamp, pageRequest);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        log.debug("Отправлен список Booking {}", bookings);
        return bookings.stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto add(BookingRequestDto bookingRequestDto, long bookerId) {
        User booker = checkUser(bookerId);
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                        .orElseThrow(() -> new NotFoundException("item"));

        validation(bookingRequestDto, item, bookerId);

        Booking booking = Mapper.fromRequestDto(bookingRequestDto, booker, item);
        log.debug("Добавлен новый Booking {}", booking);
        return Mapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto setBookingApprove(long bookingId, long ownerId, boolean approved) {
        checkUser(ownerId);
        Booking booking = bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId)
                .orElseThrow(() -> new NotFoundException("booking"));

        if (!booking.getState().equals(BookingStatus.WAITING)) throw new ValidationException("approved");

        if (approved) {
            booking.setState(BookingStatus.APPROVED);
        } else booking.setState(BookingStatus.REJECTED);
        log.debug("Статус Booking {} обновлен", booking);
        return Mapper.toDto(bookingRepository.save(booking));
    }

    private void validation(BookingRequestDto bookingRequestDto, Item item, long bookerId) {
        if (bookerId == item.getOwner().getId()) throw new NotFoundException("owner");

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

    private User checkUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user"));
    }
}
