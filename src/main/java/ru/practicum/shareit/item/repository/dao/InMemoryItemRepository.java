package ru.practicum.shareit.item.repository.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final Map <Long, Item> items;
    private long id = 0L;

    @Override
    public Item getById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item add(Item item) {
        long id = getNewId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        long id = item.getId();
        items.put(id, item);
        return item;
    }

    @Override
    public void deleteById(long id) {
        items.remove(id);
    }

    @Override
    public void deleteAll() {
        items.clear();
    }

    private long getNewId() {
        return id++;
    }
}
