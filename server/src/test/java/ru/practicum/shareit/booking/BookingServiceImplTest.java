package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AlreadyBusyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;
import ru.practicum.shareit.util.PaginationAndSortParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private final LocalDateTime timestamp = LocalDateTime.now().plusMinutes(1);
    private final long ownerId = 1;
    private final long bookerId = 2;
    private final long bookingId = 1;
    private final long itemId = 1;

    @BeforeEach
    public void createEntity() {
        owner = User.builder()
                .id(ownerId)
                .name("owner")
                .email("owner@email.ru")
                .build();

        booker = User.builder()
                .id(bookerId)
                .name("booker")
                .email("booker@email.ru")
                .build();

        item = Item.builder()
                .id(itemId)
                .name("item")
                .description("item desc")
                .owner(owner)
                .available(true)
                .build();

        booking = Booking.builder()
                .id(1)
                .start(timestamp)
                .end(timestamp.plusSeconds(1))
                .item(item)
                .booker(booker)
                .state(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1)
                .start(timestamp)
                .end(timestamp.plusSeconds(1))
                .item(Mapper.toDto(item))
                .booker(Mapper.toDto(booker))
                .status(BookingStatus.WAITING)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(timestamp)
                .end(timestamp.plusSeconds(1))
                .build();
    }

    @Test
    public void shouldReturnBookingById() {
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.getBookingById(bookingId, bookerId))
                .thenReturn(Optional.ofNullable(booking));

        Assertions.assertEquals(bookingDto, bookingService.getBookingById(bookingId, bookerId));
    }

    @Test
    public void shouldThrowExceptionWhenGetBookingByIdWithUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(bookingId, 99));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenGetBookingByIdWithUnknownBooking() {
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(99, ownerId));

        Assertions.assertEquals("booking", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldReturnBookingsByBookerStateAll() {
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(0, 1, "start");
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findAllByBookerId(bookerId, pageRequest))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByBooker(bookerId, "ALL", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByBookerStateWaiting() {
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(0, 1, "start");
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findAllByBookerIdAndState(bookerId, BookingStatus.WAITING, pageRequest))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByBooker(bookerId, "WAITING", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByBookerStateRejected() {
        booking.setState(BookingStatus.REJECTED);
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(0, 1, "start");
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findAllByBookerIdAndState(bookerId, BookingStatus.REJECTED, pageRequest))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByBooker(bookerId, "REJECTED", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByBookerStateCurrent() {
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByBooker(bookerId, "CURRENT", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByBookerStatePast() {
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsBefore(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByBooker(bookerId, "PAST", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByBookerStateFuture() {
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));

        when(bookingRepository.findAllByBookerIdAndStartIsAfter(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByBooker(bookerId, "FUTURE", 0, 1));
    }

    @Test
    public void shouldThrowExceptionWhenGetAllBookingWithUnknownBooker() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByBooker(99, "ALL", 0, 1));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldReturnBookingsByOwnerStateAll() {
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(0, 1, "start");
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerId(ownerId, pageRequest))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByOwner(ownerId, "ALL", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByOwnerStateWaiting() {
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(0, 1, "start");
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndState(ownerId, BookingStatus.WAITING, pageRequest))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByOwner(ownerId, "WAITING", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByOwnerStateRejected() {
        booking.setState(BookingStatus.REJECTED);
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(0, 1, "start");
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndState(ownerId, BookingStatus.REJECTED, pageRequest))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByOwner(ownerId, "REJECTED", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByOwnerStateCurrent() {
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByOwner(ownerId, "CURRENT", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByOwnerStatePast() {
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsBefore(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByOwner(ownerId, "PAST", 0, 1));
    }

    @Test
    public void shouldReturnBookingsByOwnerStateFuture() {
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfter(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(List.of(bookingDto),
                bookingService.getAllBookingsByOwner(ownerId, "FUTURE", 0, 1));
    }

    @Test
    public void shouldThrowExceptionWhenGetAllBookingWithUnknownOwner() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByOwner(99, "ALL", 0, 1));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldAddBooking() {
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        Assertions.assertEquals(bookingDto, bookingService.add(bookingRequestDto, bookerId));
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.add(bookingRequestDto, 99));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenUnknownItem() {
        bookingRequestDto.setItemId(99);
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.add(bookingRequestDto, bookerId));

        Assertions.assertEquals("item", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenBookerIdEqOwnerId() {
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.add(bookingRequestDto, ownerId));

        Assertions.assertEquals("owner", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final AlreadyBusyException exception = Assertions.assertThrows(AlreadyBusyException.class,
                () -> bookingService.add(bookingRequestDto, bookerId));

        Assertions.assertEquals("booking", exception.getParameter());
        Assertions.assertEquals("занят", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenStartNull() {
        bookingRequestDto.setStart(null);
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.add(bookingRequestDto, bookerId));

        Assertions.assertEquals("time", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenEndNull() {
        bookingRequestDto.setEnd(null);
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.add(bookingRequestDto, bookerId));

        Assertions.assertEquals("time", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenEndBeforeStart() {
        bookingRequestDto.setEnd(timestamp.minusSeconds(1));
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.add(bookingRequestDto, bookerId));

        Assertions.assertEquals("time", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenEndBeforeNow() {
        bookingRequestDto.setEnd(timestamp.minusMinutes(1));
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.add(bookingRequestDto, bookerId));

        Assertions.assertEquals("time", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenStartEqEnd() {
        bookingRequestDto.setEnd(timestamp);
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.add(bookingRequestDto, bookerId));

        Assertions.assertEquals("time", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionAddBookingWhenStartBeforeNow() {
        bookingRequestDto.setStart(timestamp.minusMinutes(1));
        when(userRepository.findById(bookerId))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.ofNullable(item));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.add(bookingRequestDto, bookerId));

        Assertions.assertEquals("time", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }

    @Test
    public void shouldSetBookingApproveTrue() {
        Booking approved = Booking.builder()
                .id(1)
                .start(timestamp)
                .end(timestamp.plusSeconds(1))
                .item(item)
                .booker(booker)
                .state(BookingStatus.APPROVED)
                .build();
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(approved);

        bookingService.setBookingApprove(bookingId, ownerId, true);

        verify(bookingRepository, Mockito.times(1))
                .save(Mockito.any(Booking.class));
    }

    @Test
    public void shouldSetBookingApproveFalse() {
        Booking rejected = Booking.builder()
                .id(1)
                .start(timestamp)
                .end(timestamp.plusSeconds(1))
                .item(item)
                .booker(booker)
                .state(BookingStatus.REJECTED)
                .build();
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(rejected);

        bookingService.setBookingApprove(bookingId, ownerId, false);

        verify(bookingRepository, Mockito.times(1))
                .save(Mockito.any(Booking.class));
    }

    @Test
    public void shouldThrowExceptionSetBookingApproveWhenUnknownUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.setBookingApprove(bookingId, 99, true));

        Assertions.assertEquals("user", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionSetBookingApproveWhenUnknownBooking() {
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.setBookingApprove(99, ownerId, true));

        Assertions.assertEquals("booking", exception.getParameter());
        Assertions.assertEquals("не найден", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionSetBookingApproveWhenBookingAlreadyApproved() {
        booking.setState(BookingStatus.APPROVED);
        when(userRepository.findById(ownerId))
                .thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findBookingByIdAndItemOwnerId(bookingId, ownerId))
                .thenReturn(Optional.ofNullable(booking));

        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.setBookingApprove(bookingId, ownerId, true));

        Assertions.assertEquals("approved", exception.getParameter());
        Assertions.assertEquals("некорректный", exception.getMessage());
    }
}