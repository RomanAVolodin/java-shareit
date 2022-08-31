package ru.practicum.shareit.requests.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class ItemRequest {

	@NonNull
	private Long id;

	@NonNull
	private String description;

	@NonNull
	private Long requesterId;

	@NonNull
	private LocalDate created;
}
