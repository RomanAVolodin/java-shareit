package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@SpringJUnitConfig({UserResponseDto.class})
public class UserDtoTest {

	@Autowired
	private JacksonTester<UserResponseDto> json;

	@Test
	void testItemDto() throws Exception {
		var dto = UserResponseDto.builder()
				.id(1L)
				.name("User1")
				.email("mail@mail.ru")
				.build();

		var result = json.write(dto);

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
		String nowTime = now.truncatedTo(ChronoUnit.SECONDS).format(dtf);
		assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(dto.getId().intValue());
		assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(dto.getName());
		assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(dto.getEmail());
	}

}
