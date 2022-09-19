package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SpringJUnitConfig({ItemResponseDto.class})
public class ItemDtoTest {

	@Autowired
	private JacksonTester<ItemResponseDto> json;

	@Test
	void testItemDto() throws Exception {
		var lastBooking = BookingResponseDto.builder().id(1L).itemId(1L).build();
		var nextBooking = BookingResponseDto.builder().id(2L).itemId(1L).build();
		var comments = List.of(
				CommentResponseDto.builder().id(1L).text("text").build(),
				CommentResponseDto.builder().id(2L).text("text").build()
		);

		var itemDto = ItemResponseDto.builder()
				.id(1L)
				.name("Точилка")
				.description("Описание")
				.available(true)
				.lastBooking(lastBooking)
				.nextBooking(nextBooking)
				.ownerId(1L)
				.requestId(1L)
				.comments(comments)
				.build();

		var result = json.write(itemDto);

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
		String nowTime = now.truncatedTo(ChronoUnit.SECONDS).format(dtf);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
		assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
		assertThat(result).extractingJsonPathValue("$.lastBooking.id").isEqualTo(1);
		assertThat(result).extractingJsonPathValue("$.nextBooking.id").isEqualTo(2);
		assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(2);

	}

}
