package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class User {

	@NonNull
	private Long id;

	@NonNull
	private String name;

	@NonNull
	private String email;
}
