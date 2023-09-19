package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User getById(long id);

    List<User> getAll();

    User add(User user);

    User update(long id, User user);

    void deleteById(long id);

    void deleteAll();
}
