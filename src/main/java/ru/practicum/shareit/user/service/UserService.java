package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto getById(long id);

    User findById(long id);

    List<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    void deleteById(long id);

    void deleteAll();
}
