package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<ItemRequestDto> getAllByUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user"));
        return itemRequestRepository.findAllByRequesterIdOrderByCreationDesc(userId).stream()
                .map(Mapper::toDto)
                .peek(this::fillByItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(int from, int size) {
        return itemRequestRepository.findAll().stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(long requestId) {
        ItemRequestDto itemRequestDto = Mapper.toDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("request")));
        fillByItemResponse(itemRequestDto);
        return itemRequestDto;
    }

    @Override
    public ItemRequestDto addRequest(long userId, ItemRequestShortDto itemRequestShortDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user"));
        LocalDateTime timestamp = LocalDateTime.now();
        ItemRequest itemRequest = itemRequestRepository.save(Mapper.fromShortDto(requester, itemRequestShortDto, timestamp));
        return Mapper.toDto(itemRequest);
    }

    private void fillByItemResponse(ItemRequestDto itemRequestDto) {
        List<ItemForRequestDto> itemResponses = itemRepository.findAllByItemRequestId(itemRequestDto.getId()).stream()
                .map(Mapper::toItemForRequestDto)
                .collect(Collectors.toList());
        itemRequestDto.setResponses(itemResponses);
    }
}
