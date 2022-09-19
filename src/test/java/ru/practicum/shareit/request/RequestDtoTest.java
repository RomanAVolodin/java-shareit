package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SpringJUnitConfig({ItemRequestResponseDto.class})
public class RequestDtoTest {

	@Autowired
	private JacksonTester<ItemRequestResponseDto> json;

	@Test
	void testItemDto() throws Exception {
		var dateToday = LocalDateTime.now();

		var requestDto = ItemRequestResponseDto.builder()
				.id(1L)
				.description("Описание")
				.requesterId(1L)
				.created(dateToday)
				.build();

		var result = json.write(requestDto);

		DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
		String nowTime = dateToday.truncatedTo(ChronoUnit.SECONDS).format(dtf);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
		assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(requestDto.getDescription());
		assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(nowTime);

	}

}
