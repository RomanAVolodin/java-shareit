package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.shared.OffsetBasedPaginator;
import ru.practicum.shareit.shared.exceptions.AccessDeniedException;
import ru.practicum.shareit.shared.exceptions.BadRequestException;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ItemService {

	private final ItemRepository itemRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;
	private final BookingRepository bookingRepository;
	private final ItemMapper mapper;
	private final CommentMapper commentMapper;

	@Autowired
	public ItemService(ItemRepository itemRepository,
					   UserRepository userRepository,
					   CommentRepository commentRepository,
					   BookingRepository bookingRepository,
					   ItemMapper itemMapper,
					   CommentMapper commentMapper
	) {
		this.itemRepository = itemRepository;
		this.userRepository = userRepository;
		this.commentRepository = commentRepository;
		this.bookingRepository = bookingRepository;
		this.mapper = itemMapper;
		this.commentMapper = commentMapper;
	}

	public List<ItemResponseDto> getAll(Long ownerId, Integer from, Integer size) {
		Pageable page = new OffsetBasedPaginator(size,from, Sort.by(Sort.Direction.DESC, "id"));
		return itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId, page).stream()
				.map(i -> enrichItemWithLastBookingsAndComments(ownerId, i))
				.map(mapper::itemToResponseWithBookings)
				.collect(Collectors.toList());
	}

	public List<ItemResponseDto> searchAvailable(String name, Integer from, Integer size) {
		if (name.isBlank()) {
			return new ArrayList<>();
		}
		Pageable page = new OffsetBasedPaginator(size,from, Sort.by(Sort.Direction.DESC, "id"));
		return itemRepository.searchAvailable(name, page).stream().map(mapper::itemToResponse).collect(Collectors.toList());
	}

	public ItemResponseDto getById(Long id) {
		var item = itemRepository.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Item was not found by id: " + id)
		);
		return mapper.itemToResponse(item);
	}

	public ItemResponseDto findByUserIdAndItemId(Long userId, Long itemId) {
		var item = itemRepository.findById(itemId).orElseThrow(
				() -> new ItemNotFoundException("Item was not found by id: " + itemId)
		);
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + userId)
		);
		return mapper.itemToResponseWithBookings(enrichItemWithLastBookingsAndComments(user.getId(), item));
	}

	private Item enrichItemWithLastBookingsAndComments(Long userId, Item item) {
		var bookings = bookingRepository.findTwoBookingByOwnerIdOrderByEndAsc(userId, item.getId());
		if (bookings.size() >= 2) {
			item.setLastBooking(bookings.get(0));
			item.setNextBooking(bookings.get(1));
		} else if (bookings.size() == 1) {
			if (bookings.get(0).getDateStart().isBefore(LocalDateTime.now())) {
				item.setLastBooking(bookings.get(0));
				item.setNextBooking(null);
			} else {
				item.setLastBooking(null);
				item.setNextBooking(bookings.get(0));
			}
		}
		var comments = commentRepository.findAllByItemId(item.getId());
		for (Comment comment : comments) {
			var user = userRepository.findById(comment.getAuthorId()).orElseThrow(
					() -> new ItemNotFoundException("User was not found by id: " + userId)
			);
			comment.setAuthor(user);
		}
		item.setComments(comments);
		return item;
	}

	@Transactional()
	public ItemResponseDto create(ItemCreateDto dto, Long ownerId) {
		var user = userRepository.findById(ownerId).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + ownerId)
		);
		var item = mapper.dtoToItem(dto, user.getId());
		var generatedItem = itemRepository.save(item);
		return mapper.itemToResponse(generatedItem);
	}

	@Transactional()
	public void delete(Long id, Long ownerId) {
		var item = itemRepository.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Item was not found by id: " + id)
		);
		if (!item.getOwnerId().equals(ownerId)) {
			throw new AccessDeniedException("Удалять можно только свои вещи");
		}
		itemRepository.delete(item);
	}

	@Transactional()
	public ItemResponseDto update(Long id, Long ownerId, ItemUpdateDto dto) {
		var item = itemRepository.findById(id).orElseThrow(
				() -> new ItemNotFoundException("Item was not found by id: " + id)
		);
		if (!item.getOwnerId().equals(ownerId)) {
			throw new AccessDeniedException("Редактировать можно только свои вещи");
		}

		if (dto.getName() != null) {
			item.setName(dto.getName());
		}
		if (dto.getDescription() != null) {
			item.setDescription(dto.getDescription());
		}
		if (dto.getAvailable() != null) {
			item.setAvailable(dto.getAvailable());
		}
		var updatedItem = itemRepository.save(item);
		return mapper.itemToResponse(updatedItem);
	}

	@Transactional()
	public CommentResponseDto addComment(Long userId, Long itemId, CommentCreateDto dto) {
		var item = itemRepository.findById(itemId).orElseThrow(
				() -> new ItemNotFoundException("Item was not found by id: " + itemId)
		);
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("User was not found by id: " + userId)
		);
		var booking = bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId());
		if (
				booking.stream()
						.noneMatch(
								b -> b.getStatus().equals(BookingStatus.APPROVED) &&
								b.getDateEnd().isBefore(LocalDateTime.now())
						)
		) {
			throw new BadRequestException("Пользователь id = " + userId + " не арендовывал предмет id = " + itemId);
		}
		var comment = commentMapper.dtoToComment(dto, itemId, user);
		var newComment = commentRepository.save(comment);
		return commentMapper.commentToResponse(comment);
	}
}
