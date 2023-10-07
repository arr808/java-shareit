package ru.practicum.shareit.item.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class CommentMapper {

    public static CommentDto getDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment getModel(CommentDto commentDto, long itemId) {
        return Comment.builder()
                .item(Item.builder().id(itemId).build())
                .text(commentDto.getText())
                .build();
    }
}
