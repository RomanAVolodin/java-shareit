package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {

	private final UserService service;

	@GetMapping
	public List<UserResponseDto> findAll() {
		return service.getAll();
	}

	@PostMapping
	public UserResponseDto create(@Valid @RequestBody UserCreateDto dto) {
		return service.create(dto);
	}

	@GetMapping("/{id}")
	public UserResponseDto findById(@PathVariable("id") Long id) {
		return service.getById(id);
	}

	@PatchMapping("/{id}")
	public UserResponseDto updateUserById(
			@PathVariable("id") Long id,
			@Valid @RequestBody UserUpdateDto dto
	) {
		return service.update(id, dto);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void deleteUserById(
			@PathVariable("id") Long id
	) {
		service.delete(id);
	}
}
