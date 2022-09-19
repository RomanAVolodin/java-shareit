package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.shared.exceptions.BadRequestException;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class BookingServiceTest {

	private BookingCreateDto bookingCreateDto;
	private Item item;
	private ItemResponseDto itemResponseDto;
	private User firstUser;
	private User secondUser;
	private Booking booking;
	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private ItemService itemService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private BookingMapper bookingMapper;
	@InjectMocks
	private BookingService service;

	@BeforeEach
	void setUp() {
		firstUser = User.builder().id(1L).name("First User").build();
		secondUser = User.builder().id(2L).name("Second User").build();
		item = Item.builder().id(1L).ownerId(firstUser.getId()).available(true).name("Ершик").build();

		bookingCreateDto = new BookingCreateDto();
		bookingCreateDto.setStart(LocalDateTime.now().plusDays(1));
		bookingCreateDto.setEnd(LocalDateTime.now().plusDays(2));
		bookingCreateDto.setItemId(item.getId());

		itemResponseDto = ItemResponseDto.builder()
				.id(item.getId())
				.name(item.getName())
				.available(item.getAvailable())
				.ownerId(item.getOwnerId())
				.build();

		booking = Booking.builder().id(1L).itemId(item.getId()).status(BookingStatus.APPROVED).build();
	}

	@Test
	void createBookingSuccessfulTest() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(secondUser));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		when(bookingMapper.bookingToResponse(any(Booking.class))).thenReturn(
				BookingResponseDto.builder().id(1L).itemId(1L).item(itemResponseDto).build()
		);
		when(bookingMapper.dtoToBooking(any(), anyLong())).thenReturn(Booking.builder().id(1L).itemId(1L).build());
		when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
		var bookingDtoResponse = service.create(bookingCreateDto, secondUser.getId());

		Assertions.assertNotNull(bookingDtoResponse);
		Assertions.assertEquals("Ершик", bookingDtoResponse.getItem().getName());
	}

	@Test
	void createByUnknownUserThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.create(bookingCreateDto, 21L)
		);
	}

	@Test
	void createWithUnknownItemThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(firstUser));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.create(bookingCreateDto, firstUser.getId()));
	}

	@Test
	void createWithBadDatingThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(secondUser));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		bookingCreateDto.setStart(LocalDateTime.now().minusDays(2));

		Assertions.assertThrows(BadRequestException.class,
				() -> service.create(bookingCreateDto, firstUser.getId()));
	}

	@Test
	void createWithBookerIsOwnerThrowException() {
		item.setOwnerId(secondUser.getId());
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(secondUser));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.create(bookingCreateDto, secondUser.getId()));
	}

	@Test
	void setStatusWithWrongUserThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.setStatus(firstUser.getId(), booking.getId(), true));
	}

	@Test
	void setStatusWithWrongBookingThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(secondUser));
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.setStatus(firstUser.getId(), booking.getId(), true));
	}

	@Test
	void setStatusWithWrongItemThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(secondUser));
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.setStatus(firstUser.getId(), booking.getId(), true));
	}

	@Test
	void setStatusWithAlreadyBookedThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(secondUser));
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

		Assertions.assertThrows(BadRequestException.class,
				() -> service.setStatus(firstUser.getId(), booking.getId(), true));
	}

	@Test
	void setStatusWithNotOwnerThrowException() {
		booking.setStatus(BookingStatus.WAITING);
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(secondUser));
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.setStatus(firstUser.getId(), booking.getId(), true));
	}

	@Test
	void setStatusSuccess() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(firstUser));
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

		service.setStatus(firstUser.getId(), booking.getId(), false);

		verify(bookingRepository, times(1)).save(any());
	}

	@Test
	void findByIdWithWrongUserThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.findById(firstUser.getId(), booking.getId()));
	}

	@Test
	void findByIdWithWrongIdThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(firstUser));
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.findById(firstUser.getId(), 11L));
	}

	@Test
	void findByIdWithWrongItemThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(firstUser));
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.findById(firstUser.getId(), 11L));
	}

	@Test
	void findByIdWithNotOwnerAndBookerThrowException() {
		booking.setBookerId(firstUser.getId());
		item.setOwnerId(firstUser.getId());
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(secondUser));
		when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
		when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.findById(secondUser.getId(), 11L));
	}

	@Test
	void findAllForUserWithWrongUserThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.findAllForUser(firstUser.getId(), BookingState.ALL, 0, 10));
	}

	@Test
	void findAllForUserSuccess() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(firstUser));
		when(bookingRepository.findAllByBookerIdOrderByDateEndDesc(anyLong())).thenReturn(List.of(booking));

		var result = service.findAllForUser(firstUser.getId(), BookingState.ALL, 0, 10);
		Assertions.assertTrue(result instanceof List);
		Assertions.assertEquals(result.size(), 1);
	}

	@Test
	void findAllForOwnerWithWrongUserThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class,
				() -> service.findAllForOwner(firstUser.getId(), BookingState.ALL, 0, 10));
	}

	@Test
	void findAllForOwnerSuccess() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(firstUser));
		when(bookingRepository.findAllByOwnerIdOrderByEndDesc(anyLong())).thenReturn(List.of(booking));

		var result = service.findAllForOwner(firstUser.getId(), BookingState.ALL, 0, 10);
		Assertions.assertTrue(result instanceof List);
		Assertions.assertEquals(result.size(), 1);
	}

}
