package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

	public User dtoToUser(UserCreateDto dto, Long id) {
		return User.builder()
				.id(id)
				.name(dto.getName())
				.email(dto.getEmail())
				.build();
	}

	public UserResponseDto userToResponse(User user) {
		return UserResponseDto.builder()
				.id(user.getId())
				.name(user.getName())
				.email(user.getEmail())
				.build();
	}
}
