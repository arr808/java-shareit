package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterIdOrderByCreationDesc(long requesterId);

    Optional<ItemRequest> findById(long requestId);

    List<ItemRequest> findAllByRequesterIdNotOrderByCreationDesc(long userId);
}
