package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.hamcrest.Matchers;
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
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class ItemControllerTest {

	private final ObjectMapper mapper = new ObjectMapper();
	private ItemResponseDto itemResponseDto;
	private ItemResponseDto itemResponseDto2;
	private ItemCreateDto itemCreateDto;
	@Mock
	private ItemService itemService;
	@InjectMocks
	private ItemController itemController;
	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		itemResponseDto = ItemResponseDto.builder().id(1L).name("Item1").available(true).ownerId(1L).build();
		itemResponseDto2 = ItemResponseDto.builder().id(1L).name("Item1").available(true).ownerId(1L).build();
		itemCreateDto = new ItemCreateDto();
		itemCreateDto.setName("name");
		itemCreateDto.setDescription("descr");
		itemCreateDto.setAvailable(true);
		itemCreateDto.setRequestId(1L);


		mvc = MockMvcBuilders
				.standaloneSetup(itemController)
				.build();
		mapper.registerModule(new JSR310Module());
	}

	@Test
	void searchAvailable() throws Exception {
		when(itemService.searchAvailable(anyString(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(itemResponseDto, itemResponseDto2));
		this.mvc.perform(get("/items/search")
						.header("X-Sharer-User-Id", "1")
						.param("from", "0")
						.param("size", "10")
						.param("text", "good")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void getAll() throws Exception {
		when(itemService.getAll(anyLong(), anyInt(), anyInt()))
				.thenReturn(Arrays.asList(itemResponseDto, itemResponseDto2));
		this.mvc.perform(get("/items")
						.header("X-Sharer-User-Id", "1")
						.param("from", "0")
						.param("size", "10")
						.param("text", "good")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void createSuccess() throws Exception {
		when(itemService.create(any(), anyLong()))
				.thenReturn(itemResponseDto);
		this.mvc.perform(post("/items")
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(itemCreateDto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", Matchers.is(itemResponseDto.getId()), Long.class));
	}

	@Test
	void createShouldFail() throws Exception {
		var dto = new ItemCreateDto();
		this.mvc.perform(post("/items")
						.header("X-Sharer-User-Id", "1")
						.content(mapper.writeValueAsString(dto))
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	void findItemById() throws Exception {
		when(itemService.findByUserIdAndItemId(1L, 1L)).thenReturn(itemResponseDto);

		this.mvc.perform(get("/items/1")
						.header("X-Sharer-User-Id", "1")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", Matchers.is(itemResponseDto.getId()), Long.class));
	}

	@Test
	void deleteItemById() throws Exception {
		this.mvc.perform(delete("/items/1")
						.header("X-Sharer-User-Id", "1")
						.characterEncoding(StandardCharsets.UTF_8)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
}


