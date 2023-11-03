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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Получен запрос GET /users");
        return userService.getAll();
    }

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users");
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId,
                          @RequestBody UserDto userDto) {
        log.info("Получен запрос PATCH /users/{}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        userService.deleteById(userId);
    }
}
