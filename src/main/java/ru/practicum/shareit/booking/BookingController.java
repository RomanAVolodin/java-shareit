package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public BookingResponseDto createNewBooking(
			@Valid @RequestBody BookingCreateDto bookingCreateDto,
			@RequestHeader("X-Sharer-User-Id") Long userId
	) {
		return bookingService.create(bookingCreateDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public BookingResponseDto setStatusBooking(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@PathVariable("bookingId") Long bookingId,
			@RequestParam(value = "approved", required = true) Boolean approved
	) {
		return bookingService.setStatus(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public BookingResponseDto findById(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@PathVariable("bookingId") Long bookingId
	) {
		return bookingService.findById(userId, bookingId);
	}

	@GetMapping()
	public List<BookingResponseDto> findAllForUser(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(value = "state", required = false) BookingState state,
			@RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
			@RequestParam(name = "size", defaultValue = "10") @Min(1) int size
	) {
		return bookingService.findAllForUser(userId, state, from, size);
	}

	@GetMapping("/owner")
	public List<BookingResponseDto> findAllForOwner(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(value = "state", required = false) BookingState state,
			@RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
			@RequestParam(name = "size", defaultValue = "10") @Min(1) int size
	) {
		return bookingService.findAllForOwner(userId, state, from, size);
	}
}
