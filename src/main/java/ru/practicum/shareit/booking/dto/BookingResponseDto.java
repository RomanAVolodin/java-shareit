package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingResponseDto {

	@NonNull
	private Long id;

	@FutureOrPresent
	private LocalDateTime start;

	@FutureOrPresent
	private LocalDateTime end;

	@NonNull
	private Long itemId;

	private ItemResponseDto item;

	private Long bookerId;

	private UserResponseDto booker;

	private BookingStatus status;
}
