package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.shared.exceptions.ErrorHandler;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class UserControllerTest {

	private final ObjectMapper mapper = new ObjectMapper();
	@InjectMocks
	private UserController controller;
	private UserCreateDto userCreateDto;
	private UserResponseDto userResponseDto;
	private MockMvc mvc;
	@Mock
	private UserService userService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(controller)
				.setControllerAdvice(new ErrorHandler())
				.build();

		userResponseDto = UserResponseDto.builder().id(1L).name("First User").email("mail@mail.ru").build();
		userCreateDto = new UserCreateDto();
		userCreateDto.setEmail("ad@min.ru");
		userCreateDto.setName("name");
	}


	@Test
	void emptyUserIsNotOk() throws Exception {
		var userDto = new UserCreateDto();

		this.mvc.perform(post("/users")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(userDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void create() throws Exception {
		when(userService.create(any())).thenReturn(userResponseDto);

		this.mvc.perform(post("/users")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(userCreateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)));
	}

	@Test
	void patchReturnNotFound() throws Exception {
		when(userService.update(any(), any())).thenThrow(new ItemNotFoundException("не найден пользователь"));
		var dto = new UserUpdateDto();
		dto.setEmail("ad@min.ru");
		dto.setName("name");

		this.mvc.perform(patch("/users/1")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(dto)))
				.andExpect(status().isNotFound());
	}
}
