package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item getById(long id);

    List<Item> getAll();

    Item add(Item item);

    Item update(long id, Item item);

    void deleteById(long id);

    void deleteAll();
}
