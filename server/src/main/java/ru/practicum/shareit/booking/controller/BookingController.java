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

    private static final String HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(HEADER) long userId) {
        log.info("Получен запрос GET /bookings/{}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader(HEADER) long bookerId,
                                                   @RequestParam int from,
                                                   @RequestParam int size) {
        log.info("Получен запрос GET /bookings?state={}", state);
        return bookingService.getAllBookingsByBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader(HEADER) long ownerId,
                                                  @RequestParam int from,
                                                  @RequestParam int size) {
        log.info("Получен запрос GET /bookings/owner?state={}", state);
        return bookingService.getAllBookingsByOwner(ownerId, state, from, size);
    }

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingRequestDto bookingRequestDto,
                      @RequestHeader(HEADER) long bookerId) {
        log.info("Получен запрос POST /bookings");
        return bookingService.add(bookingRequestDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto bookingReview(@PathVariable long bookingId,
                                    @RequestParam boolean approved,
                                    @RequestHeader(HEADER) long ownerId) {
        log.info("Получен запрос PATCH /bookings/{}?approved={}", bookingId, approved);
        return bookingService.setBookingApprove(bookingId, ownerId, approved);
    }
}
