package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequestShortDto {

    @NotBlank
    private String description;
    private LocalDateTime creation;
}
