package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getById(long id);

    List<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    void deleteById(long id);

    void deleteAll();
}
