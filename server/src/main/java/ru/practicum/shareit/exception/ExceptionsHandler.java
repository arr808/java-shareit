package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.user.controller.UserController;

@Slf4j
@RestControllerAdvice(assignableTypes = {ItemController.class,
                                         UserController.class,
                                         BookingController.class,
                                         ItemRequestController.class})
public class ExceptionsHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        ErrorResponse response = new ErrorResponse(e.getParameter(), e.getMessage());
        log.debug("Ошибка валидации {}", response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        ErrorResponse response = new ErrorResponse(e.getParameter(), e.getMessage());
        log.debug("Ошибка не найдено {}", response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final AlreadyExistException e) {
        ErrorResponse response = new ErrorResponse(e.getParameter(), e.getMessage());
        log.debug("Ошибка конфликт {}", response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusyException(final AlreadyBusyException e) {
        ErrorResponse response = new ErrorResponse(e.getParameter(), e.getMessage());
        log.debug("Ошибка нет доступа {}", response);
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStateException(final UnsupportedStatusException e) {
        ErrorResponse response = new ErrorResponse(e.getParameter(), e.getMessage());
        log.debug("Ошибка состояния {}", response);
        return response;
    }
}
