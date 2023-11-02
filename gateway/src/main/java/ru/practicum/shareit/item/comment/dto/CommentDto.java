package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private long id;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
