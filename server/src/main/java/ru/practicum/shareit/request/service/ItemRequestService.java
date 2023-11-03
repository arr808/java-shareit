package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDto> getAllByUser(long userId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto getById(long userId, long requestId);

    ItemRequestDto addRequest(long userId, ItemRequestShortDto itemRequestShortDto);
}
