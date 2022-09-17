package ru.practicum.shareit.item.storage.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.dao.ItemsStorageDao;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Qualifier("memory")
public class ItemsInMemoryStorage implements ItemsStorageDao {

	private final HashMap<Long, Item> items = new HashMap<>();

	@Override
	public List<Item> findAll(Long ownerId) {
		return items.values().stream().filter(
				item -> item.getOwnerId().equals(ownerId)
		).collect(Collectors.toList());
	}

	@Override
	public List<Item> searchAvailable(String searchField) {
		return items.values().stream().filter(
				i -> i.getAvailable() && !searchField.isBlank() &&
						(
								i.getName().toLowerCase().contains(searchField.toLowerCase()) ||
										i.getDescription().toLowerCase().contains(searchField.toLowerCase())
						)
		).collect(Collectors.toList());
	}

	@Override
	public Optional<Item> findById(Long id) {
		return Optional.ofNullable(items.get(id));
	}

	@Override
	public Item add(Item item) {
		items.put(item.getId(), item);
		return item;
	}

	@Override
	public Item update(Item item) {
		items.put(item.getId(), item);
		return item;
	}

	@Override
	public void delete(Long id) {
		items.remove(id);
	}

	@Override
	public boolean existsById(Long id) {
		return items.containsKey(id);
	}
}
