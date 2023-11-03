package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
public class BookingRequestDto {

    private long itemId;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
