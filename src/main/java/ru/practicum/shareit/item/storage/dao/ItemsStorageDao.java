package ru.practicum.shareit.item.storage.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemsStorageDao {

	List<Item> findAll(Long ownerUId);

	List<Item> searchAvailable(String searchField);

	Optional<Item> findById(Long id);

	Item add(Item item);

	Item update(Item item);

	void delete(Long id);

	boolean existsById(Long id);
}
