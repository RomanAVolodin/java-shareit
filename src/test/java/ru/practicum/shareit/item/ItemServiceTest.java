package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.shared.exceptions.AccessDeniedException;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class ItemServiceTest {
	User firstUser;
	User secondUser;
	Item item;
	ItemCreateDto itemCreateDto;
	ItemResponseDto itemResponseDto;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private ItemMapper mapper;
	@Mock
	private CommentMapper commentMapper;
	@InjectMocks
	private ItemService itemService;

	@BeforeEach
	void setUp() {
		firstUser = User.builder().id(1L).name("First User").build();
		secondUser = User.builder().id(2L).name("Second User").build();

		item = Item.builder().id(1L).ownerId(firstUser.getId()).available(true).name("Ершик").description("Descr").build();
		itemCreateDto = new ItemCreateDto(item.getName(), item.getDescription(), true, 1L);
		itemResponseDto = ItemResponseDto.builder()
				.id(item.getId())
				.name(item.getName())
				.available(true)
				.ownerId(firstUser.getId())
				.build();
	}

	@Test
	void createShouldSuccess() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(firstUser));
		when(itemRepository.save(any())).thenReturn(item);
		when(mapper.dtoToItem(any(), anyLong())).thenReturn(item);
		when(mapper.itemToResponse(any())).thenReturn(itemResponseDto);

		Assertions.assertEquals(itemService.create(itemCreateDto, 3L).getId(), 1);
	}

	@Test
	void checkOwnerShouldExistThrowException() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.create(itemCreateDto, 1L));
	}

	@Test
	void checkGetByIdShouldFail() {
		when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.getById(1L));
	}

	@Test
	void checkGetByIdShouldSuccess() {
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
		when(mapper.itemToResponse(any())).thenReturn(itemResponseDto);

		Assertions.assertEquals(itemService.getById(1L).getId(), 1);
	}

	@Test
	void checkDeleteByIdShouldFail() {
		when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class, () -> itemService.delete(1L, firstUser.getId()));
	}

	@Test
	void checkDeleteByWrongUserIdShouldFail() {
		when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

		Assertions.assertThrows(AccessDeniedException.class, () -> itemService.delete(1L, secondUser.getId()));
	}
}
