package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
@SpringJUnitConfig({BookingResponseDto.class})
public class BookingResponseDtoTest {

	@Autowired
	private JacksonTester<BookingResponseDto> json;

	@Test
	void testItemDto() throws Exception {

		BookingResponseDto bookingResponseDto = BookingResponseDto.builder()
				.id(1L)
				.start(LocalDateTime.now().minusDays(2))
				.end(LocalDateTime.now().minusDays(1))
				.item(ItemResponseDto.builder().id(7L).name("Test item").available(true).ownerId(1L).build()).status(BookingStatus.APPROVED)
				.booker(UserResponseDto.builder().id(5L).name("User1").build())
				.itemId(7L)
				.build();

		JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);


		LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
		LocalDateTime beforeYesterday = LocalDateTime.now().minusDays(2);
		DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
		String yesterdayStr = yesterday.truncatedTo(ChronoUnit.SECONDS).format(dtf);
		String beforeYesterdayStr = beforeYesterday.truncatedTo(ChronoUnit.SECONDS).format(dtf);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(beforeYesterdayStr);
		assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(yesterdayStr);
		assertThat(result).extractingJsonPathValue("$.booker.id").isEqualTo(5);
		assertThat(result).extractingJsonPathValue("$.status").isEqualTo("APPROVED");
		assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo(7);
		assertThat(result).extractingJsonPathValue("$.item.name").isEqualTo("Test item");
	}
}
