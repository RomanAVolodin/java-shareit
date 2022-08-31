package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class BookingResponseDto {

	@NonNull
	private Long id;

	@FutureOrPresent
	private LocalDate start;

	@FutureOrPresent
	private LocalDate end;

	@NonNull
	private Long item;

	@NonNull
	private Long booker;

	@NonNull
	private BookingStatus status;
}
