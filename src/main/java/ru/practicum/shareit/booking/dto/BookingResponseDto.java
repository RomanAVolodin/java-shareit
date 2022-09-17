package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

	@NonNull
	private Long id;

	@FutureOrPresent
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime start;

	@FutureOrPresent
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime end;

	@NonNull
	private Long itemId;

	private ItemResponseDto item;

	private Long bookerId;

	private UserResponseDto booker;

	private BookingStatus status;
}
