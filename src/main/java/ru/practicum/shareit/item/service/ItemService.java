package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto getById(long itemId, long userId);

    Item findById(long itemId);

    List<OwnerItemDto> getAll(long userId);

    List<ItemDto> searchByText(String text);

    ItemDto add(ItemDto itemDto, long userId);

    CommentDto addComment(long itemId, long userId, CommentDto commentDto);

    ItemDto update(ItemDto itemDto, long userId);

    void deleteById(long itemId, long userId);

    void deleteAll();
}
