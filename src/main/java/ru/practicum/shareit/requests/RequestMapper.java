package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RequestMapper {

	private final UserMapper userMapper;

	public ItemRequest dtoToRequest(ItemRequestCreateDto dto, Long id, Long requesterId, LocalDate created) {
		return ItemRequest.builder()
				.id(id)
				.description(dto.getDescription())
				.requesterId(requesterId)
				.created(created)
				.build();
	}

	public ItemRequestResponseDto requestToResponse(ItemRequest request) {
		return ItemRequestResponseDto.builder()
				.id(request.getId())
				.description(request.getDescription())
				.requesterId(request.getRequesterId())
				.created(request.getCreated())
				.build();
	}
}
