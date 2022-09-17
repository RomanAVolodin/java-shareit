package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.shared.exceptions.EntityExistsException;
import ru.practicum.shareit.shared.exceptions.ErrorHandler;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class BookingControllerTest {

	private final ObjectMapper mapper = new ObjectMapper();
	BookingCreateDto bookingCreateDto;
	BookingResponseDto bookingResponseDto;
	String tomorrowStr;
	String afterTomorrowStr;
	User firstUser;
	User secondUser;
	Item item;
	@Mock
	private BookingService bookingService;
	@InjectMocks
	private BookingController controller;
	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders
				.standaloneSetup(controller)
				.setControllerAdvice(new ErrorHandler())
				.build();

		mapper.findAndRegisterModules()
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
		LocalDateTime afterTomorow = LocalDateTime.now().plusDays(2);
		DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
		tomorrowStr = tomorrow.truncatedTo(ChronoUnit.SECONDS).format(dtf);
		afterTomorrowStr = afterTomorow.truncatedTo(ChronoUnit.SECONDS).format(dtf);

		firstUser = User.builder().id(1L).name("First User").build();
		secondUser = User.builder().id(2L).name("Second User").build();
		item = Item.builder().id(1L).ownerId(firstUser.getId()).available(true).name("Ершик").build();

		bookingCreateDto = new BookingCreateDto();
		bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
		bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
		bookingCreateDto.setItemId(item.getId());

		var itemResponseDto = ItemResponseDto.builder()
				.id(item.getId())
				.name(item.getName())
				.available(item.getAvailable())
				.ownerId(item.getOwnerId())
				.build();

		var booking = Booking.builder().id(1L).itemId(item.getId()).status(BookingStatus.APPROVED).build();

		bookingResponseDto = BookingResponseDto.builder()
				.id(1L)
				.itemId(item.getId())
				.item(itemResponseDto)
				.start(tomorrow)
				.end(afterTomorow)
				.bookerId(firstUser.getId())
				.booker(UserResponseDto.builder().id(firstUser.getId()).build())
				.item(itemResponseDto)
				.status(BookingStatus.APPROVED)
				.build();
	}

	@Test
	void givenAnInvalidBookingtDtoShouldThrowError() throws Exception {
		var bookingDto = new BookingCreateDto();
		mvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", "2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(bookingDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void givenNotFoundShouldThrowError() throws Exception {

		given(bookingService.findById(anyLong(), anyLong())).willThrow(new ItemNotFoundException("не найден"));

		var bookingDto = new BookingCreateDto();
		mvc.perform(get("/bookings/3")
						.header("X-Sharer-User-Id", "2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(bookingDto)))
				.andExpect(status().isNotFound());
	}

	@Test
	void givenConflictShouldThrowError() throws Exception {
		given(bookingService.findById(anyLong(), anyLong())).willThrow(new EntityExistsException("нельзя запрашивать"));

		var bookingDto = new BookingCreateDto();
		mvc.perform(get("/bookings/3")
						.header("X-Sharer-User-Id", "2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(bookingDto)))
				.andExpect(status().isConflict());
	}

	@Test
	void createBookingReturnsOkBooking() throws Exception {
		when(bookingService.create(any(), anyLong())).thenReturn(bookingResponseDto);

		mvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", "2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(bookingCreateDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.start", is(tomorrowStr)))
				.andExpect(jsonPath("$.end", is(afterTomorrowStr)))
				.andExpect(jsonPath("$.booker.id", is(firstUser.getId().intValue())))
				.andExpect(jsonPath("$.status", is("APPROVED")))
				.andExpect(jsonPath("$.item.id", is(item.getId().intValue())))
				.andExpect(jsonPath("$.item.name", is(item.getName())));
	}

	@Test
	void createBookingReturnsNotOkPastBooking() throws Exception {
		var bookingDto = new BookingCreateDto();
		bookingDto.setStart(LocalDateTime.now().minusDays(2));
		bookingDto.setEnd(LocalDateTime.now().minusDays(1));
		bookingDto.setItemId(2L);

		mvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", "2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(bookingDto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void givenAnInvalidBookingIDPatchDtoShouldThrowError() throws Exception {
		when(bookingService.setStatus(anyLong(), anyLong(), anyBoolean())).thenThrow(new ItemNotFoundException("не найдено"));

		mvc.perform(patch("/bookings/2?approved=true")
						.header("X-Sharer-User-Id", "2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(bookingCreateDto)))
				.andExpect(status().isNotFound());
	}

	@Test
	void givenValidInputShouldReturnOk() throws Exception {
		List<BookingResponseDto> bookings = List.of(bookingResponseDto);
		when(bookingService.findAllForOwner(anyLong(), any(), anyInt(), anyInt()))
				.thenReturn(bookings);

		mvc.perform(get("/bookings/owner")
						.header("X-Sharer-User-Id", "2")
						.param("from", "0")
						.param("size", "10")
						.param("status", "somestatus")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	void givenValidInputShouldReturnOkByUser() throws Exception {
		List<BookingResponseDto> bookings = List.of(bookingResponseDto);
		when(bookingService.findAllForUser(anyLong(), any(), anyInt(), anyInt()))
				.thenReturn(bookings);

		mvc.perform(get("/bookings")
						.header("X-Sharer-User-Id", "2")
						.param("from", "0")
						.param("size", "10")
						.param("state", BookingState.WAITING.toString())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}
}
