package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.util.PaginationAndSortParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        List<ItemRequestDto> result = itemRequestRepository.findAllByRequesterIdOrderByCreationDesc(userId).stream()
                .map(Mapper::toDto)
                .peek(this::fillByItems)
                .collect(Collectors.toList());
        log.debug("Отправлен список ItemRequestDto {}", result);
        return result;
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        Pageable pageRequest = PaginationAndSortParams.getPageableDesc(from, size, "creation");
        List<ItemRequestDto> result = itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest).stream()
                .map(Mapper::toDto)
                .peek(this::fillByItems)
                .collect(Collectors.toList());
        log.debug("Отправлен список ItemRequestDto {}", result);
        return result;
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user"));
        ItemRequestDto result = Mapper.toDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("request")));
        fillByItems(result);
        log.debug("Отправлен ItemRequestDto {}", result);
        return result;
    }

    @Override
    public ItemRequestDto addRequest(long userId, ItemRequestShortDto itemRequestShortDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user"));
        LocalDateTime timestamp = LocalDateTime.now();
        ItemRequest itemRequest = itemRequestRepository.save(Mapper.fromShortDto(requester, itemRequestShortDto, timestamp));
        ItemRequestDto result = Mapper.toDto(itemRequest);
        log.debug("Отправлен ItemRequestDto {}", result);
        return result;
    }

    private void fillByItems(ItemRequestDto itemRequestDto) {
        List<ItemForRequestDto> items = itemRepository.findAllByItemRequestId(itemRequestDto.getId()).stream()
                .map(Mapper::toItemForRequestDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(items);
    }
}
