package ru.practicum.services.admin_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.model.Comment;
import ru.practicum.exception.comment_exception.CommentNotFoundException;
import ru.practicum.services.admin_service.service.AdminCommentService;
import ru.practicum.services.private_service.repository.PrivateCommentRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final PrivateCommentRepository commentRepository;

    @Override
    @Transactional
    public String deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException("Коммент не найден"));
        commentRepository.deleteById(commentId);
        return "Коммент принудительно удален администратром.";
    }
}
