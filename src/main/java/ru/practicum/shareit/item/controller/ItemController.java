package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public ItemDto getById(@PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items/{}", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items");
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByName(@RequestParam String text,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос GET /items/search?text={}", text);
        return itemService.searchByText(text.toLowerCase(), userId);
    }

    @PostMapping
    public ItemDto add(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос POST /items");
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос PATCH /items/{}", itemId);
        return itemService.update(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteById(@PathVariable long itemId,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос DELETE /items/{}", itemId);
        itemService.deleteById(itemId, userId);
    }

    @DeleteMapping
    public void deleteAll() {
        log.info("Получен запрос DELETE /items");
        itemService.deleteAll();
    }
}
