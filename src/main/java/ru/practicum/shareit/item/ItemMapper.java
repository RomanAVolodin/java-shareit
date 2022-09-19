package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

@Component
public class ItemMapper {

	private final BookingMapper bookingMapper;
	private final CommentMapper commentMapper;

	@Autowired
	public ItemMapper(@Lazy BookingMapper bookingMapper, @Lazy CommentMapper commentMapper) {
		this.bookingMapper = bookingMapper;
		this.commentMapper = commentMapper;
	}

	public Item dtoToItem(ItemCreateDto dto, Long ownerId) {
		return Item.builder()
				.name(dto.getName())
				.description(dto.getDescription())
				.available(dto.getAvailable())
				.requestId(dto.getRequestId())
				.ownerId(ownerId)
				.build();
	}

	public ItemResponseDto itemToResponse(Item item) {
		return ItemResponseDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.ownerId(item.getOwnerId())
				.requestId(item.getRequestId())
				.comments(item.getComments().stream().map(commentMapper::commentToResponse).collect(Collectors.toList()))
				.build();
	}

	public ItemResponseDto itemToResponseWithBookings(Item item) {
		return ItemResponseDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.ownerId(item.getOwnerId())
				.requestId(item.getRequestId())
				.lastBooking(item.getLastBooking() != null ? bookingMapper.bookingToResponse(item.getLastBooking()) : null)
				.nextBooking(item.getNextBooking() != null ? bookingMapper.bookingToResponse(item.getNextBooking()) : null)
				.comments(item.getComments().stream().map(commentMapper::commentToResponse).collect(Collectors.toList()))
				.build();
	}
}
