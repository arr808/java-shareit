package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getById(long itemId, long userId);

    List<ItemDto> getAll(long userId);

    List<ItemDto> searchByText(String text, long userId);

    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(long itemId, ItemDto itemDto, long userId);

    void deleteById(long itemId, long userId);

    void deleteByUserId(long userId);

    void deleteAll();
}
