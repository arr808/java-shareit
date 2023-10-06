package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "where b.id = ?1 " +
            "and (b.booker.id = ?2 " +
            "or i.ownerId = ?2)")
    Optional<Booking> getBookingById(long bookingId, long userId);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId); //state ALL

    List<Booking> findAllByBookerIdAndStateOrderByStartDesc(long bookerId, BookingState state); //state WAITING or REJECTED

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId,
                                                                                   LocalDateTime startTime,
                                                                                   LocalDateTime endTime); //state CURRENT

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByStartDesc(long bookerId,
                                                                                    LocalDateTime startTime,
                                                                                    LocalDateTime endTime); //state PAST

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId,
                                                                      LocalDateTime startTime); //state FUTURE

    Optional<Booking> findBookingByIdAndItemOwnerId(long bookingId, long ownerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId); //state ALL

    List<Booking> findAllByItemOwnerIdAndStateOrderByStartDesc(long ownerId, BookingState state); //state WAITING or REJECTED

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId,
                                                                                      LocalDateTime startTime,
                                                                                      LocalDateTime endTime); //state CURRENT

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByStartDesc(long ownerId,
                                                                                       LocalDateTime startTime,
                                                                                       LocalDateTime endTime); //state PAST

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId,
                                                                         LocalDateTime startTime); //state FUTURE

    Booking findFirstByItemIdAndStartAfterAndStateNotOrderByStartAsc(long itemId, LocalDateTime now, BookingState state);

    Booking findFirstByItemIdAndStartBeforeAndStateNotOrderByEndDesc(long itemId, LocalDateTime now, BookingState state);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStateAndEndIsBefore(long itemId,
                                                                         long bookerId,
                                                                         BookingState state,
                                                                         LocalDateTime now);
}
