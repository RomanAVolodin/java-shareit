package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.storage.dao.ItemsStorageDao;
import ru.practicum.shareit.shared.BaseIdCountable;
import ru.practicum.shareit.shared.exceptions.AccessDeniedException;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.storage.dao.UsersStorageDao;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ItemService {

	private final ItemsStorageDao itemsStorage;
	private final UsersStorageDao usersStorage;
	private final BaseIdCountable idGenerator;
	private final ItemMapper mapper;

	@Autowired
	public ItemService(@Qualifier("memory") ItemsStorageDao itemsStorage,
					   @Qualifier("memory") UsersStorageDao usersStorage,
					   BaseIdCountable idGenerator,
					   ItemMapper mapper
	) {
		this.itemsStorage = itemsStorage;
		this.usersStorage = usersStorage;
		this.idGenerator = idGenerator;
		this.mapper = mapper;
	}

	public List<ItemResponseDto> getAll(Long ownerId) {
		return itemsStorage.findAll(ownerId).stream().map(mapper::itemToResponse).collect(Collectors.toList());
	}

	public List<ItemResponseDto> searchAvailable(String name) {
		return itemsStorage.searchAvailable(name).stream().map(mapper::itemToResponse).collect(Collectors.toList());
	}

	public ItemResponseDto getById(Long id) {
		var item = itemsStorage.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Item was not found by id: " + id)
		);
		return mapper.itemToResponse(item);
	}

	public ItemResponseDto create(ItemCreateDto dto, Long ownerId) {
		var user = usersStorage.findById(ownerId).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + ownerId)
		);
		var item = mapper.dtoToItem(dto, user, idGenerator.getNextId());
		var generatedItem = itemsStorage.add(item);
		return mapper.itemToResponse(generatedItem);
	}

	public void delete(Long id, Long ownerId) {
		var item = itemsStorage.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Item was not found by id: " + id)
		);
		if (!item.getOwner().getId().equals(ownerId)) {
			throw new AccessDeniedException("Удалять можно только свои вещи");
		}
		itemsStorage.delete(id);
	}

	public ItemResponseDto update(Long id, Long ownerId, ItemUpdateDto dto) {
		var item = itemsStorage.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Item was not found by id: " + id)
		);
		if (!item.getOwner().getId().equals(ownerId)) {
			throw new AccessDeniedException("Редактировать можно только свои вещи");
		}

		if (dto.getName() != null) {
			item.setName(dto.getName());
		}
		if (dto.getDescription() != null) {
			item.setDescription(dto.getDescription());
		}
		if (dto.getAvailable() != null) {
			item.setAvailable(dto.getAvailable());
		}

		return mapper.itemToResponse(item);
	}
}
