package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.comment.CommentClient;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private final CommentClient commentClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemClient itemClient, CommentClient commentClient) {
        this.itemClient = itemClient;
        this.commentClient = commentClient;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable long itemId,
                                          @RequestHeader(HEADER) long userId) {
        log.info("Получен запрос GET /items/{}", itemId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER) long userId,
                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос GET /items");
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByText(@RequestParam String text,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                      @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос GET /items/search?text={}", text);
        return itemClient.searchByText(text.toLowerCase(), from, size);
    }

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody ItemDto itemDto,
                       @RequestHeader(HEADER) long userId) {
        log.info("Получен запрос POST /items");
        return itemClient.add(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(HEADER) long userId) {
        log.info("Получен запрос POST /items/{}/comment", itemId);
        return commentClient.add(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(HEADER) long userId) {
        log.info("Получен запрос PATCH /items/{}", itemId);
        return itemClient.update(itemId, itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteById(@PathVariable long itemId,
                           @RequestHeader(HEADER) long userId) {
        log.info("Получен запрос DELETE /items/{}", itemId);
        return itemClient.deleteById(itemId, userId);
    }
}
