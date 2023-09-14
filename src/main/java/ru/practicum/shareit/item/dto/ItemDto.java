package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {

    private long id;
    private String name;
    private String description;
    private long rentTimes;
    private boolean available;
}
