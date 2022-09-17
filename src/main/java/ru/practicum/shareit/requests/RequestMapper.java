package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.model.ItemRequest;


import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestMapper {

	private final ItemMapper itemMapper;

	public ItemRequest dtoToRequest(ItemRequestCreateDto dto, Long requesterId) {
		return ItemRequest.builder()
				.description(dto.getDescription())
				.requesterId(requesterId)
				.created(LocalDateTime.now())
				.build();
	}

	public ItemRequestResponseDto requestToResponse(ItemRequest request) {
		return ItemRequestResponseDto.builder()
				.id(request.getId())
				.description(request.getDescription())
				.requesterId(request.getRequesterId())
				.items(request.getItems().stream().map(itemMapper::itemToResponse).collect(Collectors.toList()))
				.created(request.getCreated())
				.build();
	}
}
