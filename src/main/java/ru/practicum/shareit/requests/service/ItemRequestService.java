package ru.practicum.shareit.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.RequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.shared.OffsetBasedPaginator;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestService {

	private final ItemRequestRepository itemRequestRepository;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final RequestMapper mapper;

	@Autowired
	public ItemRequestService(ItemRequestRepository itemRequestRepository,
							  UserRepository userRepository,
							  ItemRepository itemRepository,
							  RequestMapper mapper
	) {

		this.itemRequestRepository = itemRequestRepository;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
		this.mapper = mapper;
	}

	public ItemRequestResponseDto getRequestByID(Long userId, long requestId) {
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + userId)
		);
		var itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
				() -> new ItemNotFoundException("Item request was not found by id: " + requestId)
		);
		return mapper.requestToResponse(enrichItemRequestWithItems(itemRequest));
	}

	public List<ItemRequestResponseDto> getRequestListByPages(Long userId, Integer from, Integer size) {
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + userId)
		);
		Pageable page = new OffsetBasedPaginator(size, from, Sort.by(Sort.Direction.DESC, "created"));

		return itemRequestRepository.findAll(page).getContent().stream()
				.filter(ir -> !ir.getRequesterId().equals(user.getId()))
				.map(this::enrichItemRequestWithItems)
				.map(mapper::requestToResponse)
				.collect(Collectors.toList());
	}

	public List<ItemRequestResponseDto> getRequestListByRequester(Long requesterId) {
		var user = userRepository.findById(requesterId).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + requesterId)
		);
		return itemRequestRepository.getItemRequestByRequesterIdOrderByCreatedDesc(user.getId()).stream()
				.map(this::enrichItemRequestWithItems)
				.map(mapper::requestToResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public ItemRequestResponseDto create(ItemRequestCreateDto itemRequestDto, Long requesterId) {
		var user = userRepository.findById(requesterId).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + requesterId)
		);
		ItemRequest itemRequest = mapper.dtoToRequest(itemRequestDto, user.getId());
		ItemRequest newRequest = itemRequestRepository.save(itemRequest);
		return mapper.requestToResponse(enrichItemRequestWithItems(newRequest));
	}

	private ItemRequest enrichItemRequestWithItems(ItemRequest itemRequest) {
		var items = itemRepository.findAllByRequestIdOrderByIdAsc(itemRequest.getId());
		itemRequest.setItems(items);
		return itemRequest;
	}
}
