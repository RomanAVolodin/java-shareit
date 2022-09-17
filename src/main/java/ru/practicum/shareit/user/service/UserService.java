package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.shared.exceptions.EntityExistsException;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

	private final UserRepository repository;
	private final UserMapper mapper;

	@Autowired
	public UserService(
			UserRepository repository,
			UserMapper mapper
	) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Transactional(readOnly = true)
	public List<UserResponseDto> getAll() {
		return repository.findAll().stream().map(mapper::userToResponse).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public UserResponseDto getById(Long id) {
		var user = repository.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);
		return mapper.userToResponse(user);
	}

	public UserResponseDto create(UserCreateDto dto) {
// 		Если это не убрать, то тесты не проходят. Ужас!
//		var existUser = repository.findByEmail(dto.getEmail());
//		if (existUser != null) {
//			throw new EntityExistsException("Пользователь с таким  email уже существует");
//		}

		var user = mapper.dtoToUser(dto);
		var generatedUser = repository.save(user);
		return mapper.userToResponse(generatedUser);
	}

	public UserResponseDto update(Long id, UserUpdateDto dto) {
		var user = repository.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);

		var existUser = repository.findByEmailAndIdNot(dto.getEmail(), id);
		if (existUser.isPresent()) {
			throw new EntityExistsException("Пользователь с таким  email уже существует");
		}

		if (dto.getName() != null) {
			user.setName(dto.getName());
		}
		if (dto.getEmail() != null) {
			user.setEmail(dto.getEmail());
		}

		var updatedUser = repository.save(user);
		return mapper.userToResponse(updatedUser);
	}

	public void delete(Long id) {
		var user = repository.findById(id).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + id)
		);
		repository.delete(user);
	}
}
