package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ru.practicum.shareit.shared.Config.SIZE_ITEM_DESCRIPTION;
import static ru.practicum.shareit.shared.Config.SIZE_ITEM_TITLE;

@Data
public class ItemCreateDto {

	@NotBlank(message = "Краткое название не может быть пустым")
	@Size(max = SIZE_ITEM_TITLE, message = "Краткое описание не должно превышать" + SIZE_ITEM_TITLE + "символов")
	private String name;

	@Size(max = SIZE_ITEM_DESCRIPTION, message = "Описание не должно превышать" + SIZE_ITEM_DESCRIPTION + "символов")
	@NotBlank(message = "Описание не может быть пустым")
	private String description;

	@NotNull
	private Boolean available;

	private Long requestId;
}
