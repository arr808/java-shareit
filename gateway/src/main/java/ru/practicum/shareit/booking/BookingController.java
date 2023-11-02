package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String HEADER = "X-Sharer-User-Id";
	@Autowired
	public BookingController(BookingClient bookingClient) {
		this.bookingClient = bookingClient;
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
									 @RequestHeader(HEADER) long userId) {
		log.info("Получен запрос GET /bookings/{}", bookingId);
		return bookingClient.getBookingById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingsByBooker(@RequestParam(name = "state", defaultValue = "ALL") String stateStr,
												   @RequestHeader(HEADER) long bookerId,
												   @PositiveOrZero @RequestParam(defaultValue = "0") int from,
												   @Positive @RequestParam(defaultValue = "20") int size) {
		log.info("Получен запрос GET /bookings?state={}", stateStr);
		BookingState state = BookingState.from(stateStr)
				.orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
		return bookingClient.getAllBookingsByBooker(bookerId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsByOwner(@RequestParam(name = "state", defaultValue = "ALL") String stateStr,
												  @RequestHeader(HEADER) long ownerId,
												  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
												  @Positive @RequestParam(defaultValue = "20") int size) {
		log.info("Получен запрос GET /bookings/owner?state={}", stateStr);
		BookingState state = BookingState.from(stateStr)
				.orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
		return bookingClient.getAllBookingsByOwner(ownerId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestBody BookingRequestDto bookingRequestDto,
								 @RequestHeader(HEADER) long bookerId) {
		log.info("Получен запрос POST /bookings");
		return bookingClient.add(bookingRequestDto, bookerId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> bookingReview(@PathVariable long bookingId,
									@RequestParam boolean approved,
									@RequestHeader(HEADER) long ownerId) {
		log.info("Получен запрос PATCH /bookings/{}?approved={}", bookingId, approved);
		return bookingClient.setBookingApprove(bookingId, ownerId, approved);
	}
}
