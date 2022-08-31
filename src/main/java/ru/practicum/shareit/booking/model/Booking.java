package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class Booking {

	@NonNull
	private Long id;

	@NonNull
	private LocalDate start;

	@NonNull
	private LocalDate end;

	@NonNull
	private Long item;

	@NonNull
	private Long booker;

	@NonNull
	private BookingStatus status;
}
