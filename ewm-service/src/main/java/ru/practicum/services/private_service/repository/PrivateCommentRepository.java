package ru.practicum.services.private_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.Optional;

public interface PrivateCommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByAuthorIdAndEventId(long userId, long eventId);

    List<Comment> findAllByEventId(long eventId);
}
