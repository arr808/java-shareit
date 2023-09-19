package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private String parameter;
    private static final String MESSAGE = "не найден";

    public NotFoundException(String parameter) {
        super(MESSAGE);
        this.parameter = parameter;
    }
}
