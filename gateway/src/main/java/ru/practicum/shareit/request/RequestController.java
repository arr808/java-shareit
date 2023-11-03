package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @Autowired
    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(HEADER) long userId) {
        log.info("Получен запрос GET /requests");
        return requestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER) long userId,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                       @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("Получен запрос GET /requests/all");
        return requestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(HEADER) long userId,
                                  @PathVariable long requestId) {
        log.info("Получен запрос GET /requests/{}", requestId);
        return requestClient.getById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(HEADER) long userId,
                                     @Valid @RequestBody ItemRequestShortDto itemRequestShortDto) {
        log.info("Получен запрос POST /requests");
        return requestClient.addRequest(userId, itemRequestShortDto);
    }
}
