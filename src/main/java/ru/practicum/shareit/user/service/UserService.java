package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.shared.BaseIdCountable;
import ru.practicum.shareit.shared.exceptions.EntityExistsException;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.storage.dao.UsersStorageDao;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {

	private final BaseIdCountable idGenerator;
	private final UserMapper mapper;
	private final UsersStorageDao storage;

	@Autowired
	public UserService(
			@Qualifier("memory") UsersStorageDao storage,
			BaseIdCountable idGenerator,
			UserMapper mapper
	) {
		this.storage = storage;
		this.idGenerator = idGenerator;
		this.mapper = mapper;
	}

	public List<UserResponseDto> getAll() {
		return storage.getAll().stream().map(mapper::userToResponse).collect(Collectors.toList());
	}

	public UserResponseDto getById(Long id) {
		var user = storage.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);
		return mapper.userToResponse(user);
	}

	public UserResponseDto create(UserCreateDto dto) {
		var existUser = storage.findByEmail(dto.getEmail());
		if (existUser.isPresent()) {
			throw new EntityExistsException("Пользователь с таким  email уже существует");
		}
		var user = mapper.dtoToUser(dto, idGenerator.getNextId());
		var generatedUser = storage.create(user);
		return mapper.userToResponse(generatedUser);
	}

	public UserResponseDto update(Long id, UserUpdateDto dto) {
		var user = storage.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);

		var existUser = storage.findByEmailExceptId(dto.getEmail(), id);
		if (existUser.isPresent()) {
			throw new EntityExistsException("Пользователь с таким  email уже существует");
		}

		if (dto.getName() != null) {
			user.setName(dto.getName());
		}
		if (dto.getEmail() != null) {
			user.setEmail(dto.getEmail());
		}

		var updatedUser = storage.update(user);
		return mapper.userToResponse(updatedUser);
	}

	public void delete(Long id) {
		var item = storage.findById(id).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + id)
		);
		storage.remove(id);
	}
}
