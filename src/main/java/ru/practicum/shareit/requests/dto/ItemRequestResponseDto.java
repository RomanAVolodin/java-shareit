package ru.practicum.shareit.requests.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDate;

@Data
@Builder
public class ItemRequestResponseDto {

	@NonNull
	private final Long id;

	@NonNull
	private final String description;

	@NonNull
	private final Long requesterId;

	@NonNull
	private final LocalDate created;
}
