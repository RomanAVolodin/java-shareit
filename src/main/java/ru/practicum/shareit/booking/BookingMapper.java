package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {

	public Booking dtoToBooking(BookingCreateDto dto, Long id) {
		return Booking.builder()
				.id(id)
				.start(dto.getStart())
				.end(dto.getEnd())
				.item(dto.getItem())
				.booker(dto.getBooker())
				.status(dto.getStatus())
				.build();
	}

	public BookingResponseDto bookingToResponse(Booking booking) {
		return BookingResponseDto.builder()
				.id(booking.getId())
				.start(booking.getStart())
				.end(booking.getEnd())
				.item(booking.getItem())
				.booker(booking.getBooker())
				.status(booking.getStatus())
				.build();
	}
}
