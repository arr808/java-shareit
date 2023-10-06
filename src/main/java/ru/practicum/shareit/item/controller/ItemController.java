package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public OwnerItemDto getById(@PathVariable long itemId,
                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items/{}", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<OwnerItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items");
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByName(@RequestParam String text) {
        log.info("Получен запрос GET /items/search?text={}", text);
        return itemService.searchByText(text.toLowerCase());
    }

    @PostMapping
    public ItemDto add(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST /items");
        return itemService.add(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST /items/{}/comment", itemId);
        return itemService.addComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH /items/{}", itemId);
        itemDto.setId(itemId);
        return itemService.update(itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос DELETE /items/{}", itemId);
        itemService.deleteById(itemId, userId);
    }
}
