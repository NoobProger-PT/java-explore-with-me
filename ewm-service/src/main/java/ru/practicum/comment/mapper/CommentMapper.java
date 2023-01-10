package ru.practicum.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.ShortCommentDto;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment mapToCommentFromNewCommentDto(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setCommentDate(LocalDateTime.now());
        return comment;
    }

    public static ShortCommentDto mapToShortCommentDtoFromComment(Comment comment) {
        ShortCommentDto shortCommentDto = new ShortCommentDto();
        shortCommentDto.setText(comment.getText());
        shortCommentDto.setAuthorName(comment.getAuthorName());
        shortCommentDto.setCommentDate(comment.getCommentDate());
        if (comment.getLikes() == null) {
            comment.setLikes(List.of());
        } else {
            shortCommentDto.setLikes(comment.getLikes().size());
        }
        if (comment.getDislikes() == null) {
            comment.setDislikes(List.of());
        } else {
            shortCommentDto.setDislikes(comment.getDislikes().size());
        }
        return shortCommentDto;
    }
}
