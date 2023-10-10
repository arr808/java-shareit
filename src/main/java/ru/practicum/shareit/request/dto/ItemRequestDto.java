package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.util.List;

@Data
@Builder
public class ItemRequestDto {

    @JsonIgnore
    private long id;
    private String description;
    private List<ItemForRequestDto> responses;
}
