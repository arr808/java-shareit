package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto getById(long itemId, long userId) {
        ItemDto result = ItemMapper.getDto(itemRepository.getById(itemId));
        log.debug("Отправлен ItemDto {}", result);
        return result;
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        List<ItemDto> result = itemRepository.getAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .map(ItemMapper::getDto)
                .collect(Collectors.toList());
        log.debug("Отправлен список ItemDto {}", result);
        return result;
    }

    @Override
    public List<ItemDto> searchByText(String text, long userId) {
        userService.getById(userId);
        List<ItemDto> result = itemRepository.getAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text) ||
                        item.getDescription().toLowerCase().contains(text))
                .map(ItemMapper::getDto)
                .collect(Collectors.toList());
        log.debug("Отправлен список ItemDto {}", result);
        return result;
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        userService.getById(userId);
        Item item = itemRepository.add(ItemMapper.getModel(itemDto, userId));
        ItemDto result = ItemMapper.getDto(item);
        log.debug("Отправлен ItemDto {}", result);
        return result;
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long userId) {
        userService.getById(userId);
        Item item = itemRepository.getById(itemId);
        if (item.getOwnerId() == userId) {
            Item updatingItem = ItemMapper.getModel(itemDto, userId);
            updatingItem.setId(itemId);

            if (updatingItem.getName() == null) updatingItem.setName(item.getName());
            if (updatingItem.getDescription() == null) updatingItem.setDescription(item.getDescription());
            if (updatingItem.getAvailable() == null) updatingItem.setAvailable(item.getAvailable());

            ItemDto result = ItemMapper.getDto(itemRepository.update(itemId, updatingItem));
            log.debug("Отправлен ItemDto {}", result);
            return result;
        }
        throw new NotFoundException("owner id");
    }

    @Override
    public void deleteById(long itemId, long userId) {
        itemRepository.deleteById(itemId);
        log.debug("Item с id = {} удален", itemId);
    }

    @Override
    public void deleteByUserId(long userId) {
        for (Item item : itemRepository.getAll()) {
            if (item.getOwnerId() == userId) itemRepository.deleteById(item.getId());
        }
        log.debug("У User id = {} удалены все Item", userId);
    }

    @Override
    public void deleteAll() {
        itemRepository.deleteAll();
        log.debug("Все элементы Item удалены");
    }
}
