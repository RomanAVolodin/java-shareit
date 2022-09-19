package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserCreateDto {

	@NotBlank(message = "Почта не может быть пустой")
	@Email(message = "Email недействительный")
	private String email;

	@NotBlank(message = "Имя не может быть пустым")
	private String name;
}
