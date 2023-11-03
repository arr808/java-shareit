package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookingById(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByBooker(long bookerId, BookingState state, int from, int size) {
        return get("?state={state}&from={from}&size={size}", bookerId, getParameters(state, from, size));
    }

    public ResponseEntity<Object> getAllBookingsByOwner(long ownerId, BookingState state, int from, int size) {
        return get("/owner?state={state}&from={from}&size={size}", ownerId, getParameters(state, from, size));
    }

    public ResponseEntity<Object> add(BookingRequestDto bookingRequestDto, long bookerId) {
        return post("", bookerId, bookingRequestDto);
    }

    public ResponseEntity<Object> setBookingApprove(long bookingId, long ownerId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, ownerId);
    }

    private Map<String, Object> getParameters(BookingState state, int from, int size) {
        return Map.of("state", state.name(),"from", from, "size", size);
    }
}
