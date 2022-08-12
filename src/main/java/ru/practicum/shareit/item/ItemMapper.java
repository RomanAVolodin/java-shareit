package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.RequestMapper;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class ItemMapper {

	private final UserMapper userMapper;

	private final RequestMapper requestMapper;

	public Item dtoToItem(ItemCreateDto dto, User owner, Long id) {
		return Item.builder()
				.id(id)
				.name(dto.getName())
				.description(dto.getDescription())
				.available(dto.getAvailable())
				.owner(owner)
				.build();
	}

	public ItemResponseDto itemToResponse(Item item) {
		return ItemResponseDto.builder()
				.id(item.getId())
				.name(item.getName())
				.description(item.getDescription())
				.available(item.getAvailable())
				.owner(userMapper.userToResponse(item.getOwner()))
				.request(item.getRequest() != null ? requestMapper.requestToResponse(item.getRequest()) : null)
				.build();
	}
}
