package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getById(long id) {
        UserDto result = UserMapper.getDto(userRepository.getById(id));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> result = userRepository.getAll().stream()
                .map(UserMapper::getDto)
                .collect(Collectors.toList());
        log.debug("Отправлен список UserDto {}", result);
        return result;
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.getModel(userDto);
        UserDto result = UserMapper.getDto(userRepository.add(user));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = userRepository.getById(id);
        User updatingUser = UserMapper.getModel(userDto);
        updatingUser.setId(id);

        if (updatingUser.getName() == null) updatingUser.setName(user.getName());
        if (updatingUser.getEmail() == null) updatingUser.setEmail(user.getEmail());

        UserDto result = UserMapper.getDto(userRepository.update(id, updatingUser));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    public void deleteById(long id) {
        userRepository.deleteById(id);
        log.debug("User с id = {} удален", id);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
        log.debug("Все элементы User удалены");
    }
}
