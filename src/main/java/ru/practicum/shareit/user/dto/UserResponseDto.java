package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

	@NonNull
	private Long id;

	@Email
	private String email;

	@NotBlank
	private String name;
}
