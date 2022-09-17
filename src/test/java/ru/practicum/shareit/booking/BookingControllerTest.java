package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ErrorHandler;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoNames;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.BookerDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class BookingControllerTest {


    @Mock
    private BookingService bookingService;

    private final ObjectMapper mapper = new ObjectMapper();
    @InjectMocks
    private BookingController controller;

    private MockMvc mvc;

    BookingDtoNames bookingDtoNames;
    BookingDto bookingDto;
    String tomorrowStr;
    String afterTomorrowStr;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ErrorHandler())
                .build();

        mapper.findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        bookingDtoNames = new BookingDtoNames(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookerDto bookerDto = new BookerDto(5L);
        ItemDtoShort itemDtoShort = new ItemDtoShort(7L, "hummer");
        bookingDtoNames.setId(1L);
        bookingDtoNames.setBooker(bookerDto);
        bookingDtoNames.setItem(itemDtoShort);
        bookingDtoNames.setStatus(Status.APPROVED);

        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 5L);
        bookingDto.setItemId(2L);

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime afterTomorow = LocalDateTime.now().plusDays(2);
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
        tomorrowStr = tomorrow.truncatedTo(ChronoUnit.SECONDS).format(dtf);
        afterTomorrowStr = afterTomorow.truncatedTo(ChronoUnit.SECONDS).format(dtf);

    }

    @Test
    void givenAnInvalidBookingtDtoShouldThrowError() throws Exception {
        BookingDto bookingDto = new BookingDto();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNotFoundShouldThrowError() throws Exception {

        given(bookingService.getById(anyLong(), anyLong())).willThrow(new NotFoundException("не найден"));

        BookingDto bookingDto = new BookingDto();
        mvc.perform(get("/bookings/3")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenConflictShouldThrowError() throws Exception {

        given(bookingService.getById(anyLong(), anyLong())).willThrow(new ConflictException("нельзя запрашивать"));

        BookingDto bookingDto = new BookingDto();
        mvc.perform(get("/bookings/3")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isConflict());
    }


    @Test
    void createBookingReturnsNotOkPastBooking() throws Exception {

        BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), 5L);
        bookingDto.setItemId(2L);


        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void createBookingReturnsOkBooking() throws Exception {

        when(bookingService.create(any(), anyLong())).thenReturn(bookingDtoNames);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(tomorrowStr)))
                .andExpect(jsonPath("$.end", is(afterTomorrowStr)))
                .andExpect(jsonPath("$.booker.id", is(5)))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.item.id", is(7)))
                .andExpect(jsonPath("$.item.name", is("hummer")));

    }

    @Test
    void givenAnInvalidBookingIDPatchtDtoShouldThrowError() throws Exception {

        when(bookingService.updateStatus(anyLong(),anyBoolean(),anyLong())).thenThrow(new NotFoundException("не найдено"));

        mvc.perform(patch("/bookings/2?approved=true")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidBookingIdPatchtDtoShouldReturnOk() throws Exception {

        when(bookingService.updateStatus(anyLong(),anyBoolean(),anyLong())).thenReturn(bookingDtoNames);

        mvc.perform(patch("/bookings/2?approved=true")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is(tomorrowStr)))
                .andExpect(jsonPath("$.end", is(afterTomorrowStr)))
                .andExpect(jsonPath("$.booker.id", is(5)))
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.item.id", is(7)))
                .andExpect(jsonPath("$.item.name", is("hummer")));
    }

    @Test
    void givenValidInputShouldReturnOk() throws Exception {

        List<BookingDtoNames> bookingDtosNames = Arrays.asList(bookingDtoNames);
        when(bookingService.checkInputStateAndGetForOwnerByState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookingDtosNames);


        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2")
                        .param("from", "0")
                        .param("size", "10")
                        .param("status", "somestatus")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(1)));
    }

    @Test
    void givenValidInputShouldReturnOkByUser() throws Exception {

        List<BookingDtoNames> bookingDtosNames = Arrays.asList(bookingDtoNames);
        when(bookingService.checkInputStateAndGetForBookingUserByState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(bookingDtosNames);


        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .param("from", "0")
                        .param("size", "10")
                        .param("state", "somestate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(1)));
    }

}
