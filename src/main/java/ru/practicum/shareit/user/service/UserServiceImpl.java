package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    private final ItemRepository itemRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public UserDto getById(long id) {
        UserDto result = UserMapper.getDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user")));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    public User findById(long id) {
        User result = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user"));
        log.debug("Отправлен User {}", result);
        return result;
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> result = userRepository.findAll().stream()
                .map(UserMapper::getDto)
                .collect(Collectors.toList());
        log.debug("Отправлен список UserDto {}", result);
        return result;
    }

    @Override
    public UserDto add(UserDto userDto) {
        validation(userDto);
        User user = UserMapper.getModel(userDto);
        UserDto result = UserMapper.getDto(userRepository.save(user));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user"));
        User updatingUser = UserMapper.getModel(userDto);
        updatingUser.setId(id);

        if (updatingUser.getName() == null) updatingUser.setName(user.getName());
        if (updatingUser.getEmail() == null) updatingUser.setEmail(user.getEmail());

        UserDto result = UserMapper.getDto(userRepository.save(updatingUser));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    public void deleteById(long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user"));
        deleteAllItemsFromUser(id);
        userRepository.deleteById(id);
        log.debug("User с id = {} удален", id);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        log.debug("Все элементы User удалены");
    }

    private void validation(UserDto userDto) {
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name == null || name.isBlank()) throw new ValidationException("name");
        if (email == null || email.isBlank()) throw new ValidationException("email");
    }

    private void deleteAllItemsFromUser(long userId) {
        for (Item item : itemRepository.findAll()) {
            if (item.getOwnerId() == userId) itemRepository.deleteById(item.getId());
        }
        log.debug("У User id = {} удалены все Item", userId);
    }
}
