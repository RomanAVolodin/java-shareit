package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ItemResponseDto {

	@NonNull
	private final Long id;

	@NotBlank
	private final String name;

	private final String description;

	@NonNull
	private final Boolean available;

	@NonNull
	private final UserResponseDto owner;

	private final ItemRequestResponseDto request;
}
