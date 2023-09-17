package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public UserController(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        log.debug("Получен запрос GET /users/{}", userId);
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("Получен запрос GET /users");
        return userService.getAll();
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        log.debug("Получен запрос POST /users");
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId,
                          @RequestBody UserDto userDto) {
        log.debug("Получен запрос PATCH /users/{}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        log.debug("Получен запрос DELETE /users/{}", userId);
        itemService.deleteByUserId(userId);
        userService.deleteById(userId);
    }

    @DeleteMapping
    public void deleteAll() {
        log.debug("Получен запрос DELETE /users");
        itemService.deleteAll();
        userService.deleteAll();
    }
}
