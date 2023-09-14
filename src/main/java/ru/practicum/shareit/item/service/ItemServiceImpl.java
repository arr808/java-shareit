package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemDto getById(long itemId, long userId) {
        Item item = itemRepository.getById(itemId);
        if (item.getOwnerId() == userId) return ItemMapper.getDto(item);
        return null;
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        return itemRepository.getAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .map(ItemMapper::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByName(String text, long userId) {
        return itemRepository.getAll().stream()
                .filter(item -> item.getOwnerId() == userId)
                .filter(item -> item.getName().contains(text) || item.getDescription().contains(text))
                .map(ItemMapper::getDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        Item item = itemRepository.add(ItemMapper.getModel(itemDto, userId));
        return ItemMapper.getDto(item);
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long userId) {
        Item item = itemRepository.getById(itemId);
        if (item.getOwnerId() == userId) {
            itemRepository.update(ItemMapper.getModel(itemDto,userId));
            return itemDto;
        }
        return null;
    }

    @Override
    public void deleteById(long itemId, long userId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public void deleteAll() {
        itemRepository.deleteAll();
    }
}
