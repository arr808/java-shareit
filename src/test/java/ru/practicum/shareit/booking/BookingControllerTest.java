package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.controller.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    BookingService bookingService;
    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;
    private final long bookingId = 1;
    private final long userId = 1;
    private static final String QUERY = "/bookings";
    private final LocalDateTime timestamp = LocalDateTime.now();

    @BeforeEach
    public void createEntity() {
        bookingDto = BookingDto.builder()
                .id(bookingId)
                .start(timestamp)
                .end(timestamp.plusSeconds(1))
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(1)
                .start(timestamp)
                .end(timestamp.plusSeconds(1))
                .build();
    }

    @Test
    public void shouldReturnBookingById() throws Exception {
        when(bookingService.getBookingById(bookingId, userId))
                .thenReturn((bookingDto));

        mvc.perform(get(QUERY + "/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));
    }

    @Test
    public void shouldReturnBookingsByBooker() throws Exception {
        when(bookingService.getAllBookingsByBooker(userId, BookingState.ALL, 0, 20))
                .thenReturn((List.of(bookingDto)));

        mvc.perform(get(QUERY + "/?state=")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    public void shouldReturnBookingsByOwner() throws Exception {
        when(bookingService.getAllBookingsByOwner(userId, BookingState.ALL, 0, 20))
                .thenReturn((List.of(bookingDto)));

        mvc.perform(get(QUERY + "/owner?state=")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));
    }

    @Test
    public void shouldAddBooking() throws Exception {
        mvc.perform(post(QUERY)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldBookingReview() throws Exception {
        mvc.perform(patch(QUERY + "/1?approved=true")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}