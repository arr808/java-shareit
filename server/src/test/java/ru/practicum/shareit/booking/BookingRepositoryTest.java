package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.PaginationAndSortParams;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private Item item;
    private User owner;
    private User booker;
    private Booking booking;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final Pageable pageRequest = PaginationAndSortParams.getPageable(0, 1);

    @BeforeEach
    public void createEntity() {
        owner = User.builder()
                .name("owner")
                .email("owner@email.ru")
                .build();

        booker = User.builder()
                .name("booker")
                .email("booker@email.ru")
                .build();

        item = Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build();

        booking = Booking.builder()
                .start(timestamp)
                .end(timestamp.plusMinutes(1))
                .item(item)
                .booker(booker)
                .state(BookingStatus.WAITING)
                .build();

        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);

    }

    @Test
    public void shouldGetBookingById() {
        Booking result = bookingRepository.getBookingById(booking.getId(), booker.getId()).get();

        Assertions.assertEquals(booking, result);
    }

    @Test
    public void shouldFindAllByBookerId() {
        List<Booking> result = bookingRepository.findAllByBookerId(booker.getId(), pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByBookerIdAndStateWaiting() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndState(booker.getId(), BookingStatus.WAITING, pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByBookerIdAndStateRejected() {
        booking.setState(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        List<Booking> result = bookingRepository.findAllByBookerIdAndState(booker.getId(), BookingStatus.REJECTED, pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByBookerIdCurrent() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfter(booker.getId(),
                        timestamp.plusSeconds(1),
                        timestamp.plusSeconds(2),
                        pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByBookerIdPast() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsBefore(booker.getId(),
                        timestamp.plusSeconds(1),
                        timestamp.plusMinutes(2),
                        pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByBookerIdFuture() {
        List<Booking> result = bookingRepository
                .findAllByBookerIdAndStartIsAfter(booker.getId(),
                        timestamp.minusMinutes(1),
                        pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindBookingByIdAndOwnerId() {
        Booking result = bookingRepository.findBookingByIdAndItemOwnerId(booking.getId(), owner.getId()).get();

        Assertions.assertEquals(booking, result);
    }

    @Test
    public void shouldFindAllByOwnerId() {
        List<Booking> result = bookingRepository.findAllByItemOwnerId(owner.getId(), pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByOwnerIdAndStateWaiting() {
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndState(owner.getId(), BookingStatus.WAITING, pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByOwnerIdAndStateRejected() {
        booking.setState(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        List<Booking> result = bookingRepository.findAllByItemOwnerIdAndState(owner.getId(), BookingStatus.REJECTED, pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByOwnerIdCurrent() {
        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(owner.getId(),
                        timestamp.plusSeconds(1),
                        timestamp.plusSeconds(2),
                        pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByOwnerIdPast() {
        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartIsBeforeAndEndIsBefore(owner.getId(),
                        timestamp.plusSeconds(1),
                        timestamp.plusMinutes(2),
                        pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindAllByOwnerIdFuture() {
        List<Booking> result = bookingRepository
                .findAllByItemOwnerIdAndStartIsAfter(owner.getId(),
                        timestamp.minusMinutes(1),
                        pageRequest);

        Assertions.assertEquals(List.of(booking), result);
    }

    @Test
    public void shouldFindFirstByItemIdAndStartAfterAndStateNot() {
        Booking result = bookingRepository
                .findFirstByItemIdAndStartAfterAndStateNotOrderByStartAsc(item.getId(),
                        timestamp.minusSeconds(10),
                        BookingStatus.REJECTED);

        Assertions.assertEquals(booking, result);
    }

    @Test
    public void shouldFindFirstByItemIdAndStartBeforeAndStateNot() {
        Booking result = bookingRepository
                .findFirstByItemIdAndStartBeforeAndStateNotOrderByEndDesc(item.getId(),
                        timestamp.plusSeconds(5),
                        BookingStatus.REJECTED);

        Assertions.assertEquals(booking, result);
    }

    @Test
    public void shouldFindFirstByItemIdAndBookerIdAndStateAndEndIsBefore() {
        Booking result = bookingRepository
                .findFirstByItemIdAndBookerIdAndStateAndEndIsBefore(item.getId(),
                        booker.getId(),
                        BookingStatus.WAITING,
                        timestamp.plusHours(1)).get();

        Assertions.assertEquals(booking, result);
    }

    @Test
    public void shouldNotAddBookingNoStart() {
        Booking booking2 = Booking.builder()
                .end(timestamp.plusMinutes(1))
                .item(item)
                .booker(booker)
                .state(BookingStatus.WAITING)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> bookingRepository.save(booking2));
    }

    @Test
    public void shouldNotAddBookingNoEnd() {
        Booking booking2 = Booking.builder()
                .start(timestamp)
                .item(item)
                .booker(booker)
                .state(BookingStatus.WAITING)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> bookingRepository.save(booking2));
    }

    @Test
    public void shouldNotAddBookingNoItem() {
        Booking booking2 = Booking.builder()
                .start(timestamp)
                .end(timestamp.plusMinutes(1))
                .booker(booker)
                .state(BookingStatus.WAITING)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> bookingRepository.save(booking2));
    }

    @Test
    public void shouldNotAddBookingNoBooker() {
        Booking booking2 = Booking.builder()
                .start(timestamp)
                .end(timestamp.plusMinutes(1))
                .item(item)
                .state(BookingStatus.WAITING)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> bookingRepository.save(booking2));
    }

    @Test
    public void shouldNotAddBookingNoState() {
        Booking booking2 = Booking.builder()
                .start(timestamp)
                .end(timestamp.plusMinutes(1))
                .item(item)
                .booker(booker)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> bookingRepository.save(booking2));
    }
}
