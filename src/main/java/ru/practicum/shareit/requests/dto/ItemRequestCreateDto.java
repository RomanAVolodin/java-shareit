package ru.practicum.shareit.requests.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ru.practicum.shareit.shared.Config.SIZE_REQUEST_DESCRIPTION;

@Getter
@Setter
public class ItemRequestCreateDto {

	@NotBlank
	@Size(max = SIZE_REQUEST_DESCRIPTION, message = "Описание не должно превышать " + SIZE_REQUEST_DESCRIPTION + " символов")
	private String description;
}
