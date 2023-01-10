package ru.practicum.services.public_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.ShortCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.services.private_service.repository.PrivateCommentRepository;
import ru.practicum.services.public_service.service.PublicCommentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {

    private final PrivateCommentRepository commentRepository;

    @Override
    public List<ShortCommentDto> getAllComments(long eventId) {
        List<ShortCommentDto> result = commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper::mapToShortCommentDtoFromComment)
                .collect(Collectors.toList());
        return result;
    }
}
