package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

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
	private final Long ownerId;

	private final Long requestId;

	private final BookingResponseDto lastBooking;

	private final BookingResponseDto nextBooking;

	@Builder.Default
	private List<CommentResponseDto> comments = new ArrayList<>();
}
