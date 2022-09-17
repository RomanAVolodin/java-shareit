package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ru.practicum.shareit.shared.Config.SIZE_COMMENT_TEXT;

@Data
public class CommentCreateDto {

	@NotBlank(message = "Текст сообщения не может быть пустым")
	@Size(max = SIZE_COMMENT_TEXT, message = "Описание не должно превышать " + SIZE_COMMENT_TEXT + " символов")
	private String text;
}
