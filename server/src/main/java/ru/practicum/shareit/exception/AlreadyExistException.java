package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class AlreadyExistException extends RuntimeException {
    private static final String MESSAGE = "уже существует";
    private String parameter;

    public AlreadyExistException(String parameter) {
        super(MESSAGE);
        this.parameter = parameter;
    }
}
