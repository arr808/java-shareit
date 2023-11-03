package ru.practicum.shareit.user;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Получен запрос GET /users");
        return userClient.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users");
        return userClient.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable long userId,
                          @Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /users/{}", userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteById(@PathVariable long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        return userClient.deleteById(userId);
    }
}
