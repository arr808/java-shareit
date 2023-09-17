package ru.practicum.shareit.item.repository.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items;
    private long id;

    @Override
    public Item getById(long itemId) {
        Item item = items.get(itemId);
        if (item != null) {
            log.debug("Отправлен item: {}", item);
            return item;
        }
        throw new NotFoundException("item");
    }

    @Override
    public List<Item> getAll() {
        List<Item> result = new ArrayList<>(items.values());
        log.debug("Отправлен список items: {}", result);
        return result;
    }

    @Override
    public Item add(Item item) {
        long localId = getNewId();
        item.setId(localId);
        items.put(localId, item);
        log.debug("В список items добавлен Item {}", item);
        return item;
    }

    @Override
    public Item update(long itemId, Item item) {
        items.put(itemId, item);
        log.debug("В списке items обновлен Item {}", item);
        return item;
    }

    @Override
    public void deleteById(long itemId) {
        items.remove(itemId);
        log.debug("Из списка items удален Item с id={}", itemId);
    }

    @Override
    public void deleteAll() {
        items.clear();
        log.debug("Список items отчищен");
    }

    private long getNewId() {
        log.info("Выделен новый id");
        return ++id;
    }
}
