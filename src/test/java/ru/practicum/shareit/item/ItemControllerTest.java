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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

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

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();

    ItemDto itemDto;
    ItemDto itemDto2;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {

        itemDto = new ItemDto("Screw", "Good screw", true);
        itemDto.setId(1L);
        itemDto = new ItemDto("Hummer", "Good hummer", false);
        itemDto.setId(2L);
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        mapper.registerModule(new JSR310Module());
    }

    // Метод, который добавляет новый объект
    @Test
    void create() throws Exception {
        when(itemService.create(any(), anyLong())).thenReturn(itemDto);

        this.mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class));
    }


    @Test
    void update() throws Exception {
        itemDto.setAvailable(false);
        when(itemService.update(any(), anyLong(), anyLong())).thenReturn(itemDto);

        this.mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", Matchers.is(itemDto.getAvailable())));
    }

    // Метод по получению всех объектов пользователя
    @Test
    void getUserItems() throws Exception {
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt())).thenReturn(Arrays.asList(itemDto,itemDto2));

        this.mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // Метод по получению одного объекта
    @Test
    void getOne() throws Exception {
        when(itemService.getItemByIdForOwnerOrForUser(1L, 1L)).thenReturn(itemDto);

        this.mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(itemDto.getId()), Long.class));

    }

    @Test
    void searchNameAndDesc() throws Exception {
        when(itemService.searchNameAndDesc(anyString(), anyInt(), anyInt())).thenReturn(Arrays.asList(itemDto2,itemDto));
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

    // Метод для добавления комментариев

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = new CommentDto();
     //   commentDto.setId(1L);
        commentDto.setText("good hummer");
     //   commentDto.setCreated(LocalDateTime.now());
        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        this.mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", Matchers.is(("good hummer"))));


    }


}


