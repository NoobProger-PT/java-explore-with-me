package ru.practicum.services.admin_service.service;

import ru.practicum.comment.dto.FullCommentDto;

public interface AdminCommentService {

    String deleteComment(long commentId);

    FullCommentDto getById(long commentId);
}
