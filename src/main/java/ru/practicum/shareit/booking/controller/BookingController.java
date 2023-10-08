package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            bookingState = BookingState.UNSUPPORTED_STATUS;
        }
        return bookingService.getAllBookingsByBooker(bookerId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос GET /bookings/owner?state={}", state);
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            bookingState = BookingState.UNSUPPORTED_STATUS;
        }
        return bookingService.getAllBookingsByOwner(ownerId, bookingState);
    }

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingRequestDto bookingRequestDto,
                      @RequestHeader("X-Sharer-User-Id") long bookerId) {
        log.info("Получен запрос POST /bookings");
        return bookingService.addBooking(bookingRequestDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingReview(@PathVariable long bookingId,
                                    @RequestParam boolean approved,
                                    @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Получен запрос PATCH /bookings/{}?approved={}", bookingId, approved);
        return bookingService.setBookingApprove(bookingId, ownerId, approved);
    }
}
