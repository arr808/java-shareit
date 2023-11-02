package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Mapper;

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
    @Transactional(readOnly = true)
    public UserDto getById(long id) {
        UserDto result = Mapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user")));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        List<UserDto> result = userRepository.findAll().stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());
        log.debug("Отправлен список UserDto {}", result);
        return result;
    }

    @Override
    @Transactional
    public UserDto add(UserDto userDto) {
        validation(userDto);
        User user = Mapper.fromDto(userDto);
        UserDto result = Mapper.toDto(userRepository.save(user));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    @Transactional
    public UserDto update(long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user"));
        User updatingUser = Mapper.fromDto(userDto);
        updatingUser.setId(id);

        if (updatingUser.getName() == null) updatingUser.setName(user.getName());
        if (updatingUser.getEmail() == null) updatingUser.setEmail(user.getEmail());

        UserDto result = Mapper.toDto(userRepository.save(updatingUser));
        log.debug("Отправлен UserDto {}", result);
        return result;
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user"));
        deleteAllItemsFromUser(id);
        userRepository.deleteById(id);
        log.debug("User с id = {} удален", id);
    }

    @Override
    @Transactional
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
            if (item.getOwner().getId() == userId) itemRepository.deleteById(item.getId());
        }
        log.debug("У User id = {} удалены все Item", userId);
    }
}
