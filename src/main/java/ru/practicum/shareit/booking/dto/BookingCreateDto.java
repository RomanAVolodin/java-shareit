package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class BookingCreateDto {

	@FutureOrPresent(message = "Дата начала не может быть в прошлом")
	private LocalDate start;

	@FutureOrPresent(message = "Дата окончания не может быть в прошлом")
	private LocalDate end;

	@NonNull
	private Long item;

	@NonNull
	private Long booker;

	@NonNull
	private BookingStatus status;
}
