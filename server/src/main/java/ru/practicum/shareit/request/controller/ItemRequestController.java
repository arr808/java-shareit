package ru.practicum.shareit.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(HEADER) long userId) {
        log.info("Получен запрос GET /requests");
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader(HEADER) long userId,
                                       @RequestParam int from,
                                       @RequestParam int size) {
        log.info("Получен запрос GET /requests/all");
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@RequestHeader(HEADER) long userId,
                                  @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/{}", requestId);
        return itemRequestService.getById(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader(HEADER) long userId,
                                     @RequestBody ItemRequestShortDto itemRequestShortDto) {
        log.info("Получен запрос POST /requests");
        return itemRequestService.addRequest(userId, itemRequestShortDto);
    }
}
