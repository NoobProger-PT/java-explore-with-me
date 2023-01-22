package ru.practicum.services.public_service.service;

import ru.practicum.comment.dto.ShortCommentDto;

import java.util.List;

public interface PublicCommentService {

    List<ShortCommentDto> getAllComments(long eventId);
}
