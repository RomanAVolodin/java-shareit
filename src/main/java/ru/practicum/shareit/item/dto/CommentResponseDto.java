package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


import java.time.LocalDateTime;


@Data
@Builder
public class CommentResponseDto {

    @NonNull
    private Long id;

    @NonNull
    private String text;

    private Long itemId;

    private Long authorId;

    private LocalDateTime created;

    private String authorName;
}
