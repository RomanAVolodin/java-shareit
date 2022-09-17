package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.practicum.shareit.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.user.dto.UserDto;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    UserController controller;

    UserDto userDto;
    MockMvc mvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();
        userDto = new UserDto("name", "some@email.com");
    }


    @Test
    void EmptyUserIsNotOk() throws Exception {
        UserDto userDto = new UserDto();

        this.mvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void create() throws Exception {
        userDto.setId(1L);
      when(userService.create(any())).thenReturn(userDto);

        this.mvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void patchReturnNotFound() throws Exception {

        when(userService.update(any(),anyLong())).thenThrow(new NotFoundException("не найден пользователь"));

        this.mvc.perform(patch("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchReturnUpdatedUser() throws Exception {

        userDto.setName("newName");
        when(userService.update(any(),anyLong())).thenReturn(userDto);

        this.mvc.perform(patch("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is("newName")));
    }

    @Test
    void returnNotFoundException() throws Exception {
        Mockito.doThrow(new NotFoundException("not found")).when(userService).delete(anyLong());
        this.mvc.perform(delete("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnEmptyUserListIsOk() throws Exception {
        List<UserDto> userDtos = new ArrayList<>();
        when(userService.getUsers()).thenReturn(userDtos);
        this.mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }
    @Test
    void return2UserListIsOk() throws Exception {
        UserDto userDto1 = new UserDto("myName","someOther@email.com");
        List<UserDto> userDtos = Arrays.asList(userDto,userDto1);
        when(userService.getUsers()).thenReturn(userDtos);
        this.mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)));
    }

    @Test
    void returnUserByNumberIsOk() throws Exception {
        userDto.setId(1L);
        when(userService.getUserById(anyLong())).thenReturn(userDto);
        this.mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void returnUserByNumberNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("не найден"));
        this.mvc.perform(get("/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
