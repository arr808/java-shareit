package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b " +
            "from Booking as b " +
            "join fetch b.item as i " +
            "where b.id = ?1 " +
            "and (b.booker.id = ?2 " +
            "or i.ownerId = ?2)")
    Optional<Booking> getBookingById(long bookingId, long userId);
}
