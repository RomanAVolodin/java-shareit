package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

	@NotBlank(message = "Почта не может быть пустой")
	@Email(message = "Email недействительный")
	private String email;

	@NotBlank(message = "Имя не может быть пустым")
	private String name;
}
