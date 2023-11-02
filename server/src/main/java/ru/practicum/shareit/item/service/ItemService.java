package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getById(long itemId, long userId);

    List<ItemDto> getAll(long userId, int from, int size);

    List<ItemDto> searchByText(String text, int from, int size);

    ItemDto add(ItemDto itemDto, long userId);

    CommentDto addComment(long itemId, long userId, CommentDto commentDto);

    ItemDto update(ItemDto itemDto, long userId);

    void deleteById(long itemId, long userId);

    void deleteAll();
}
