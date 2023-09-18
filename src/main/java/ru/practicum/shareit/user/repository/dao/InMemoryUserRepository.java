package ru.practicum.shareit.user.repository.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users;
    private long id;

    @Override
    public User getById(long userId) {
        User user = users.get(userId);
        if (user == null) throw new NotFoundException("user");
        log.debug("Отправлен user: {}", user);
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> result = new ArrayList<>(users.values());
        log.debug("Отправлен список users: {}", result);
        return result;
    }

    @Override
    public User add(User user) {
        checkEmailDuplicates(0, user.getEmail());
        long localId = getNewId();
        user.setId(localId);
        users.put(localId, user);
        log.debug("В список users добавлен User {}", user);
        return user;
    }

    @Override
    public User update(long userId, User user) {
        checkEmailDuplicates(userId, user.getEmail());
        users.put(userId, user);
        log.debug("В списке users обновлен User {}", user);
        return user;
    }

    @Override
    public void deleteById(long userId) {
        User user = getById(userId);
        users.remove(userId);
        log.debug("Из списка users удален User с id={}", userId);
    }

    @Override
    public void deleteAll() {
        users.clear();
        id = 0;
        log.debug("Список users отчищен");
    }

    private long getNewId() {
        log.info("Выделен новый id");
        return ++id;
    }

    private void checkEmailDuplicates(long userId, String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && userId != user.getId()) throw new AlreadyExistException("email");
        }
        log.info("Email {} уникальный", email);
    }
}
