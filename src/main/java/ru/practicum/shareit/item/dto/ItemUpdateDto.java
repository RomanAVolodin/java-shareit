package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.Size;

import static ru.practicum.shareit.shared.Config.SIZE_ITEM_DESCRIPTION;
import static ru.practicum.shareit.shared.Config.SIZE_ITEM_TITLE;

@Data
public class ItemUpdateDto {

	@Size(max = SIZE_ITEM_TITLE, message = "Краткое описание не должно превышать" + SIZE_ITEM_TITLE + "символов")
	private final String name;

	@Size(max = SIZE_ITEM_DESCRIPTION, message = "Описание не должно превышать" + SIZE_ITEM_DESCRIPTION + "символов")
	private final String description;

	private final Boolean available;
}
