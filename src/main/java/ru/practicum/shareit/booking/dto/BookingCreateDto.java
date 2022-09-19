package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingCreateDto {

	@FutureOrPresent(message = "Дата начала не может быть в прошлом")
	@NotNull
	private LocalDateTime start;

	@FutureOrPresent(message = "Дата окончания не может быть в прошлом")
	@NotNull
	private LocalDateTime end;

	@NonNull
	private Long itemId;
}
