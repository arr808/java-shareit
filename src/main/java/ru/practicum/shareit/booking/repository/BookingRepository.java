package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "where b.id = ?1 " +
            "and (b.booker.id = ?2 " +
            "or i.owner.id = ?2)")
    Optional<Booking> getBookingById(long bookingId, long userId);

    List<Booking> findAllByBookerId(long bookerId, Pageable pageable); //state ALL

    List<Booking> findAllByBookerIdAndState(long bookerId, BookingStatus state, Pageable pageable); //state WAITING or REJECTED

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long bookerId,
                                                                 LocalDateTime startTime,
                                                                 LocalDateTime endTime,
                                                                 Pageable pageable); //state CURRENT

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsBefore(long bookerId,
                                                                  LocalDateTime startTime,
                                                                  LocalDateTime endTime,
                                                                  Pageable pageable); //state PAST

    List<Booking> findAllByBookerIdAndStartIsAfter(long bookerId,
                                                   LocalDateTime startTime,
                                                   Pageable pageable); //state FUTURE

    Optional<Booking> findBookingByIdAndItemOwnerId(long bookingId, long ownerId);

    List<Booking> findAllByItemOwnerId(long ownerId, Pageable pageable); //state ALL

    List<Booking> findAllByItemOwnerIdAndState(long ownerId, BookingStatus state, Pageable pageable); //state WAITING or REJECTED

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long ownerId,
                                                                    LocalDateTime startTime,
                                                                    LocalDateTime endTime,
                                                                    Pageable pageable); //state CURRENT

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsBefore(long ownerId,
                                                                     LocalDateTime startTime,
                                                                     LocalDateTime endTime,
                                                                     Pageable pageable); //state PAST

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId,
                                                      LocalDateTime startTime,
                                                      Pageable pageable); //state FUTURE

    Booking findFirstByItemIdAndStartAfterAndStateNotOrderByStartAsc(long itemId, LocalDateTime now, BookingStatus state);

    Booking findFirstByItemIdAndStartBeforeAndStateNotOrderByEndDesc(long itemId, LocalDateTime now, BookingStatus state);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStateAndEndIsBefore(long itemId,
                                                                         long bookerId,
                                                                         BookingStatus state,
                                                                         LocalDateTime now);
}
