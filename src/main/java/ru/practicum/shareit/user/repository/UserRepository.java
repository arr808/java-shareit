package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {

    User getById(long id);

    List<User> getAll();

    User add(User user);

    User update(User user);

    void deleteById(long id);

    void deleteAll();
}
