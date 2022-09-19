package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserUpdateDto {

	@Email(message = "Email недействительный")
	private String email;

	private String name;
}
