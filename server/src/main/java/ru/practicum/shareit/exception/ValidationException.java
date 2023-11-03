package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private static final String MESSAGE = "некорректный";
    private String parameter;

    public ValidationException(String parameter) {
        super(MESSAGE);
        this.parameter = parameter;
    }
}
