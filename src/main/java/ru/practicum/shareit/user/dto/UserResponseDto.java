package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserResponseDto {

	@NonNull
	private final Long id;

	@Email
	private final String email;

	@NotBlank
	private final String name;
}
