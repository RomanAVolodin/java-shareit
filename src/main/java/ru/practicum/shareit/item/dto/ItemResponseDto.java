package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {

	@NonNull
	private Long id;

	@NotBlank
	private String name;

	private String description;

	@NonNull
	private Boolean available;

	@NonNull
	private Long ownerId;

	private Long requestId;

	private BookingResponseDto lastBooking;

	private BookingResponseDto nextBooking;

	@Builder.Default
	private List<CommentResponseDto> comments = new ArrayList<>();
}
