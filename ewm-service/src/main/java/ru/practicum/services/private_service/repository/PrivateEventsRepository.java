package ru.practicum.services.private_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.State;
import ru.practicum.event.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PrivateEventsRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByUserId(long userId, Pageable pageable);

    Optional<Event> findByIdAndUserId(long eventId, long userId);

    List<Event> findAllByCategoryId(long catId);

    List<Event> findAllByAnnotationOrDescriptionContainingIgnoreCaseAndCategoryInAndState(String annotation,
                                                                                          String description,
                                                                                          Collection<Category> category,
                                                                                          State state,
                                                                                          Pageable pageable);

    List<Event> findAllByUserIdIn(List<Long> users, Pageable pageable);
}
