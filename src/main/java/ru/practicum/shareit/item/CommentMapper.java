package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public Comment dtoToComment(CommentCreateDto dto, Long itemId, User user) {
        return Comment.builder()
                .text(dto.getText())
                .itemId(itemId)
                .authorId(user.getId())
                .author(user)
                .build();
    }

    public CommentResponseDto commentToResponse(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItemId())
                .authorId(comment.getAuthorId())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}
