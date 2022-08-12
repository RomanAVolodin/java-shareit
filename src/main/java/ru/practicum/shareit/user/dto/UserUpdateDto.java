package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateDto {

	@Email(message = "Email недействительный")
	private final String email;

	private final String name;
}
