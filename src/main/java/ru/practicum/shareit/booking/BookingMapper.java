package ru.practicum.shareit.booking;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.storage.UserRepository;

@Component
@NoArgsConstructor
public class BookingMapper {

	private UserMapper userMapper;
	private UserRepository userRepository;
	private ItemRepository itemRepository;
	private ItemMapper itemMapper;

	@Autowired
	public BookingMapper(
			@Lazy ItemMapper itemMapper,
			@Lazy UserMapper userMapper,
			UserRepository userRepository,
			ItemRepository itemRepository
	) {
		this.itemMapper = itemMapper;
		this.userMapper = userMapper;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
	}

	public Booking dtoToBooking(BookingCreateDto dto, Long userId) {
		return Booking.builder()
				.dateStart(dto.getStart())
				.dateEnd(dto.getEnd())
				.itemId(dto.getItemId())
				.bookerId(userId)
				.build();
	}

	public BookingResponseDto bookingToResponse(Booking booking) {
		var item = itemRepository.getById(booking.getItemId());
		var booker = userRepository.getById(booking.getBookerId());
		return BookingResponseDto.builder()
				.id(booking.getId())
				.start(booking.getDateStart())
				.end(booking.getDateEnd())
				.itemId(booking.getItemId())
				.item(itemMapper.itemToResponse(item))
				.booker(userMapper.userToResponse(booker))
				.bookerId(booking.getBookerId())
				.status(booking.getStatus())
				.build();
	}
}
