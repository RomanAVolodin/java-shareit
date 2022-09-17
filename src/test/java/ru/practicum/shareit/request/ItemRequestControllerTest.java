package ru.practicum.shareit.request;

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
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.shared.exceptions.ErrorHandler;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class ItemRequestControllerTest {

	private final ObjectMapper mapper = new ObjectMapper();
	ItemRequestResponseDto itemRequestResponseDto;
	ItemRequestResponseDto itemRequestResponseDto2;
	ItemRequestCreateDto itemRequestCreateDto;
	@Mock
	private ItemRequestService itemRequestService;
	@InjectMocks
	private ItemRequestController controller;
	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(controller)
				.setControllerAdvice(new ErrorHandler())
				.build();

		itemRequestResponseDto = ItemRequestResponseDto.builder()
				.id(1L)
				.description("desc")
				.requesterId(1L)
				.created(LocalDateTime.now())
				.build();
		itemRequestResponseDto2 = ItemRequestResponseDto.builder()
				.id(2L)
				.description("desc")
				.requesterId(1L)
				.created(LocalDateTime.now())
				.build();
		itemRequestCreateDto = new ItemRequestCreateDto("desc");
	}

	@Test
	void shouldReturnOk() throws Exception {
		when(itemRequestService.getRequestListByPages(any(), anyInt(), anyInt()))
				.thenReturn(List.of(itemRequestResponseDto, itemRequestResponseDto2));

		this.mvc.perform(get("/requests/all")
						.header("X-Sharer-User-Id", "2")
						.param("from", "0")
						.param("size", "10")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(status().isOk());
	}

	@Test
	void givenAnInvalidRequestDtoShouldThrowError() throws Exception {
		var itemRequestDto = new ItemRequestCreateDto();
		mvc.perform(post("/requests")
						.header("X-Sharer-User-Id", "2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(itemRequestDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createSuccess() throws Exception {
		when(itemRequestService.create(any(), any()))
				.thenReturn(itemRequestResponseDto);

		mvc.perform(post("/requests")
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(itemRequestCreateDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
				.andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())));
	}

	@Test
	void shouldReturnList() throws Exception {
		when(itemRequestService.getRequestListByRequester(any()))
				.thenReturn(List.of(itemRequestResponseDto, itemRequestResponseDto2));

		mvc.perform(get("/requests")
						.header("X-Sharer-User-Id", "1")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].description", is(itemRequestResponseDto.getDescription())));
	}

	@Test
	void shouldReturnNotFound() throws Exception {
		given(itemRequestService.getRequestByID(anyLong(), anyLong())).willThrow(new ItemNotFoundException("не найден"));
		mvc.perform(get("/requests/{id}", 2)
						.header("X-Sharer-User-Id", "1")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
}
