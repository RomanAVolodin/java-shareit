package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class UserServiceTest {

	User firstUser;
	UserResponseDto userResponseDto;
	UserCreateDto userCreateDto;
	@Mock
	private UserRepository userRepository;
	@Mock
	private UserMapper mapper;
	@InjectMocks
	private UserService service;

	@BeforeEach
	void setUp() {
		firstUser = User.builder().id(1L).name("First User").email("mail@mail.ru").build();
		userResponseDto = UserResponseDto.builder().id(1L).name("First User").email("mail@mail.ru").build();
		userCreateDto = new UserCreateDto("mail@mail.ru", "First User");
	}

	@Test
	void checkGetByIdThrowException() {
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class, () -> service.getById(1L));
	}

	@Test
	void checkCreateSuccess() {
		when(mapper.dtoToUser(any())).thenReturn(firstUser);
		when(userRepository.save(any())).thenReturn(firstUser);
		when(mapper.userToResponse(any())).thenReturn(userResponseDto);

		var result = service.create(userCreateDto);
		Assertions.assertEquals(result.getId(), userResponseDto.getId());
		Assertions.assertEquals(result.getEmail(), userResponseDto.getEmail());
		Assertions.assertEquals(result.getName(), userResponseDto.getName());
	}

	@Test
	void checkDeleteThrowException() {
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class, () -> service.delete(1L));
	}
}
