package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class AlreadyBusyException extends RuntimeException {
    private static final String MESSAGE = "занят";
    private String parameter;

    public AlreadyBusyException(String parameter) {
        super(MESSAGE);
        this.parameter = parameter;
    }
}
