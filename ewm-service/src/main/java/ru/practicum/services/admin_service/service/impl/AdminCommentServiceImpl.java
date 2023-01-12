package ru.practicum.services.admin_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.FullCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.exception.comment_exception.CommentNotFoundException;
import ru.practicum.services.admin_service.service.AdminCommentService;
import ru.practicum.services.private_service.repository.PrivateCommentRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final PrivateCommentRepository commentRepository;

    @Override
    public String deleteComment(long commentId) {
        Comment comment = getCommentIfExists(commentId);
        commentRepository.deleteById(commentId);
        return "Коммент принудительно удален администратром.";
    }

    @Override
    public FullCommentDto getById(long commentId) {
        Comment comment = getCommentIfExists(commentId);
        return CommentMapper.mapToFullCommentDtoFromComment(comment);
    }

    private Comment getCommentIfExists(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException("Коммент не найден"));
        return comment;
    }
}
