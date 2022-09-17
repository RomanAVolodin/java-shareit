package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.shared.exceptions.BadRequestException;
import ru.practicum.shareit.shared.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

	private final BookingRepository bookingRepository;

	private final UserRepository userRepository;

	private final ItemRepository itemRepository;

	private final BookingMapper bookingMapper;

	@Transactional
	public BookingResponseDto create(BookingCreateDto bookingCreateDto, Long userId) {
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);
		var item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(
				() -> new ItemNotFoundException("Предмет не найден по id")
		);
		if (!item.getAvailable()) {
			throw new BadRequestException("Этот предмет не доступен для аренды");
		}

		if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())
				|| bookingCreateDto.getStart().isBefore(LocalDateTime.now())
				|| bookingCreateDto.getEnd().isBefore(LocalDateTime.now())) {
			throw new BadRequestException("Продолжительность аренды не верно указана");
		}

		if (Objects.equals(item.getOwnerId(), userId)) {
			throw new ItemNotFoundException("Пользователь id = "
					+ userId + " является владельцем предмета id = " + bookingCreateDto.getItemId());
		}
		var booking = bookingMapper.dtoToBooking(bookingCreateDto, user.getId());
		var newBooking = bookingRepository.save(booking);
		return bookingMapper.bookingToResponse(newBooking);
	}

	@Transactional
	public BookingResponseDto setStatus(Long userId, Long bookingId, Boolean approved) {
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);
		var booking = bookingRepository.findById(bookingId).orElseThrow(
				() -> new ItemNotFoundException("Бронирование не найдено по id")
		);
		var item = itemRepository.findById(booking.getItemId()).orElseThrow(
				() -> new ItemNotFoundException("Предмет не найден по id")
		);
		if (booking.getStatus() == BookingStatus.APPROVED && approved) {
			throw new BadRequestException("Бронирование уже подтверждено");
		}
		if (!item.getOwnerId().equals(user.getId())) {
			throw new ItemNotFoundException("Подтверждение доступно только владельцу");
		}

		if (approved.equals(Boolean.TRUE)) {
			booking.setStatus(BookingStatus.APPROVED);
		} else {
			booking.setStatus(BookingStatus.REJECTED);
		}
		var updatedBooking = bookingRepository.save(booking);
		return bookingMapper.bookingToResponse(updatedBooking);
	}

	@Transactional(readOnly = true)
	public BookingResponseDto findById(Long userId, Long bookingId) {
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);
		var booking = bookingRepository.findById(bookingId).orElseThrow(
				() -> new ItemNotFoundException("Бронирование не найдено по id")
		);
		var item = itemRepository.findById(booking.getItemId()).orElseThrow(
				() -> new ItemNotFoundException("Предмет не найден по id")
		);
		if (!booking.getBookerId().equals(user.getId()) && !item.getOwnerId().equals(userId)) {
			throw new ItemNotFoundException("Пользователь не автор бронирования и не владелец предмета");
		}
		return bookingMapper.bookingToResponse(booking);
	}

	@Transactional(readOnly = true)
	public List<BookingResponseDto> findAllForUser(Long userId, BookingState state, Integer from, Integer size) {
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);
		if (state == null) {
			state = BookingState.ALL;
		}
		return processBookingsForUser(
				user.getId(),
				state,
				bookingRepository::findAllByBookerIdOrderByDateEndDesc,
				bookingRepository::findAllByBookerIdAndStatusOrderByDateEndDesc,
				from,
				size
		);
	}

	@Transactional(readOnly = true)
	public List<BookingResponseDto> findAllForOwner(Long userId, BookingState state, Integer from, Integer size) {
		var user = userRepository.findById(userId).orElseThrow(
				() -> new ItemNotFoundException("Пользователь не найден по id")
		);
		if (state == null) {
			state = BookingState.ALL;
		}

		return processBookingsForUser(
				user.getId(),
				state,
				bookingRepository::findAllByOwnerIdOrderByEndDesc,
				bookingRepository::findAllByOwnerIdAndStatusOrderByEndDesc,
				from,
				size
		);
	}

	private List<BookingResponseDto> processBookingsForUser(
			Long userId,
			BookingState state,
			LongFunction<List<Booking>> findAllByUserIdOrderByEndDesc,
			BiFunction<Long, BookingStatus, List<Booking>> findAllByUserIdAndStatusOrderByEndDesc,
			Integer from,
			Integer size
	) {
		List<Booking> resultBookings;
		List<Booking> bookings = findAllByUserIdOrderByEndDesc.apply(userId);
		switch (state) {
			case ALL:
				resultBookings = bookings;
				break;
			case WAITING:
				resultBookings = findAllByUserIdAndStatusOrderByEndDesc.apply(userId, BookingStatus.WAITING);
				break;
			case REJECTED:
				resultBookings = findAllByUserIdAndStatusOrderByEndDesc.apply(userId, BookingStatus.REJECTED);
				break;
			case PAST:
				resultBookings = bookings.stream()
						.filter(b -> b.getDateEnd().isBefore(LocalDateTime.now()))
						.collect(Collectors.toList());
				break;
			case CURRENT:
				resultBookings = bookings.stream()
						.filter(b -> b.getDateStart()
								.isBefore(LocalDateTime.now()) && b.getDateEnd().isAfter(LocalDateTime.now()))
						.collect(Collectors.toList());
				break;
			case FUTURE:
				resultBookings = bookings.stream()
						.filter(b -> b.getDateEnd().isAfter(LocalDateTime.now()))
						.collect(Collectors.toList());
				break;
			default:
				throw new BadRequestException("Unknown state: " + state);
		}
		var result = resultBookings.stream().map(bookingMapper::bookingToResponse).collect(Collectors.toList());
		return result.subList(from, Math.min(from + size, result.size()));
	}
}
