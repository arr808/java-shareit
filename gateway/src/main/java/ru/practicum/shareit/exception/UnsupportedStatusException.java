package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class UnsupportedStatusException extends RuntimeException {

    private static final String MESSAGE = "state";
    private String parameter;

    public UnsupportedStatusException(String parameter) {
        super(MESSAGE);
        this.parameter = parameter;
    }
}
