package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.RequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class RequestServiceTest {

	User firstUser;
	ItemRequest itemRequest;
	Item item;
	ItemRequestResponseDto itemRequestResponseDto;
	@Mock
	private ItemRequestRepository itemRequestRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private RequestMapper mapper;
	@InjectMocks
	private ItemRequestService service;

	@BeforeEach
	void setUp() {
		firstUser = User.builder().id(1L).name("First User").build();
		itemRequest = ItemRequest.builder().id(1L).build();
		item = Item.builder().id(1L).build();
		itemRequestResponseDto = ItemRequestResponseDto.builder()
				.id(1L)
				.requesterId(1L)
				.created(LocalDateTime.now())
				.description("descr")
				.build();
	}

	@Test
	void checkGetRequestByIdThrowException() {
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class, () -> service.getRequestByID(1L, 1L));
	}

	@Test
	void checkGetRequestByIdItemDoesNotExistThrowException() {
		when(userRepository.findById(any())).thenReturn(Optional.of(firstUser));
		when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class, () -> service.getRequestByID(1L, 1L));
	}

	@Test
	void checkGetRequestByIDSuccess() {
		when(userRepository.findById(any())).thenReturn(Optional.of(firstUser));
		when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));
		when(itemRepository.findAllByRequestIdOrderByIdAsc(any())).thenReturn(List.of(item));
		when(mapper.requestToResponse(any())).thenReturn(itemRequestResponseDto);

		Assertions.assertEquals(service.getRequestByID(3L, 1L).getId(), 1);
	}

	@Test
	void checkGetRequestListByPagesThrowException() {
		when(userRepository.findById(any())).thenReturn(Optional.empty());

		Assertions.assertThrows(ItemNotFoundException.class, () -> service.getRequestListByPages(1L, 1, 10));
	}
}
