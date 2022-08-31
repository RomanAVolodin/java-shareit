package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Builder
@Data
public class Item {

	@NonNull
	private Long id;

	@NotBlank
	private String name;

	@NonNull
	private String description;

	@NonNull
	private Boolean available;

	@NonNull
	private User owner;

	private ItemRequest request;
}
