package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemDto {

    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private long requestId;
    private List<CommentDto> comments;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
}
