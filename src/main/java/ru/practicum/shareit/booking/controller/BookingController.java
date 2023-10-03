package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /bookings/{}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Получен запрос GET /bookings?state={}", state);
        return bookingService.getAllBookingsByBooker(bookerId, RequestState.valueOf(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос GET /bookings/owner?state={}", state);
        return bookingService.getAllBookingsByOwner(ownerId, RequestState.valueOf(state));
    }

    @PostMapping
    public BookingDto addBooking(@RequestBody Booking booking,
                      @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Получен запрос POST /bookings");
        return bookingService.addBooking(booking, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingReview(@PathVariable long bookingId,
                                    @RequestParam boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос PATCH /bookings/{}?approved={}", bookingId, approved);
        return bookingService.bookingReview(bookingId, ownerId, approved);
    }
}
