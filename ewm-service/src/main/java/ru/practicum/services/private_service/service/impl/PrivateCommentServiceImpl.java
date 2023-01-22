package ru.practicum.services.private_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.ShortCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.exception.comment_exception.CommentNotFoundException;
import ru.practicum.exception.event_exception.EventNotFoundException;
import ru.practicum.exception.user_exception.UserNotFound;
import ru.practicum.services.admin_service.repository.AdminUsersRepository;
import ru.practicum.services.private_service.repository.PrivateCommentRepository;
import ru.practicum.services.private_service.repository.PrivateEventsRepository;
import ru.practicum.services.private_service.service.PrivateCommentService;
import ru.practicum.user.model.User;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final PrivateEventsRepository eventsRepository;
    private final AdminUsersRepository usersRepository;
    private final PrivateCommentRepository commentRepository;


    @Override
    public ShortCommentDto add(long userId, long eventId, NewCommentDto newCommentDto) {
        User user = getUserIfExists(userId);
        Event event = getEventIfExists(eventId);
        Comment comment = CommentMapper.mapToCommentFromNewCommentDto(newCommentDto);
        comment.setAuthorName(user.getName());
        comment.setAuthor(user);
        comment.setEvent(event);
        return CommentMapper.mapToShortCommentDtoFromComment(commentRepository.save(comment));
    }

    @Override
    public ShortCommentDto get(long userId, long eventId) {
        Comment comment = findCommentByIds(userId, eventId);
        return CommentMapper.mapToShortCommentDtoFromComment(comment);
    }

    @Override
    public String delete(long userId, long eventId, long commentId) {
        Comment comment = findCommentByIds(userId, eventId);
        commentRepository.deleteById(commentId);
        return "Коммент удален";
    }

    @Override
    public ShortCommentDto update(long userId, long eventId, UpdateCommentDto updateCommentDto) {
        Comment comment = findCommentByIds(userId, eventId);
        if (comment.getId() != updateCommentDto.getId()) {
            throw new CommentNotFoundException("Ид не равны.");
        }
        comment.setText(updateCommentDto.getText());
        return CommentMapper.mapToShortCommentDtoFromComment(comment);
    }

    @Override
    public ShortCommentDto addLike(long userId, long commentId) {
        User user = getUserIfExists(userId);
        Comment comment = getCommentIfExists(commentId);
        List<User> likeList = comment.getLikes();
        List<User> dislikes = comment.getDislikes();
        if (dislikes.contains(user)) {
            dislikes.remove(user);
            likeList.add(user);
            comment.setLikes(likeList);
            comment.setDislikes(dislikes);
            return CommentMapper.mapToShortCommentDtoFromComment(comment);
        }
        if (likeList.contains(user)) {
            likeList.remove(user);
        } else {
            likeList.add(user);
        }
        comment.setLikes(likeList);
        return CommentMapper.mapToShortCommentDtoFromComment(comment);
    }

    @Override
    public ShortCommentDto addDislike(long userId, long commentId) {
        User user = getUserIfExists(userId);
        Comment comment = getCommentIfExists(commentId);
        List<User> dislikeList = comment.getDislikes();
        List<User> likeList = comment.getLikes();
        if (likeList.contains(user)) {
            likeList.remove(user);
            dislikeList.add(user);
            comment.setLikes(likeList);
            comment.setDislikes(dislikeList);
            return CommentMapper.mapToShortCommentDtoFromComment(comment);
        }
        if (dislikeList.contains(user)) {
            dislikeList.remove(user);
        } else {
            dislikeList.add(user);
        }
        dislikeList.add(user);
        comment.setLikes(dislikeList);
        return CommentMapper.mapToShortCommentDtoFromComment(comment);
    }

    private User getUserIfExists(long userId) {
        User user = usersRepository.findById(userId).orElseThrow(() ->
                new UserNotFound("Пользователь с id: " + userId + " не найден"));
        return user;
    }

    private Event getEventIfExists(long eventId) {
        Event event = eventsRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Такого ивента нет."));
        return event;
    }

    private Comment getCommentIfExists(long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new CommentNotFoundException("Такого коммента нет."));
        return comment;
    }

    private Comment findCommentByIds(long userId, long eventId) {
        Comment comment = commentRepository.findByAuthorIdAndEventId(userId, eventId).orElseThrow(() ->
                new CommentNotFoundException("Коммент, написанный пользователем " + userId + " посту " + eventId + ", не найден."));
        return comment;
    }
}
