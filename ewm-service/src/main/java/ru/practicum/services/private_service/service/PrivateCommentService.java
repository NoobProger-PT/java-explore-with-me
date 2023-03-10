package ru.practicum.services.private_service.service;

import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.ShortCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;

public interface PrivateCommentService {

    ShortCommentDto add(long userId, long eventId, NewCommentDto newCommentDto);

    ShortCommentDto get(long userId, long eventId);

    String delete(long userId, long eventId, long commentId);

    ShortCommentDto update(long userId, long eventId, UpdateCommentDto updateCommentDto);

    ShortCommentDto addLike(long userId, long commentId);

    ShortCommentDto addDislike(long userId, long commentId);
}
