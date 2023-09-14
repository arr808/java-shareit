package ru.practicum.shareit.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.user.controller.UserController;

@RestControllerAdvice(assignableTypes = {ItemController.class, UserController.class})
public class ExceptionsHandler {

}
