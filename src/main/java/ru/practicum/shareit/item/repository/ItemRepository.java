package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select new ru.practicum.shareit.item.dto.ItemDto(i.id, i.name, i.description, i.available) " +
           "from Item as i " +
           "where i.available = true and " +
           "(lower(i.name) like lower(concat('%', ?1, '%')) or " +
           "lower(i.description) like lower(concat('%', ?1, '%')))")
    List<ItemDto> searchByText(String text);
}