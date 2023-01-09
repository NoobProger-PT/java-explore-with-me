package ru.practicum.services.private_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.State;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PrivateEventsRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(long eventId, long userId);

    List<Event> findAllByAnnotationOrDescriptionContainingIgnoreCaseAndCategoryInAndStateAndPaidAndEventDateBetween(String annotation,
                                                                                                                    String description,
                                                                                                                    Collection<Category> category,
                                                                                                                    State state,
                                                                                                                    boolean paid,
                                                                                                                    LocalDateTime start,
                                                                                                                    LocalDateTime end,
                                                                                                                    Pageable pageable);

    List<Event> findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(Collection<Long> initiatorId,
                                                                                 Collection<State> state,
                                                                                 Collection<Long> category,
                                                                                 LocalDateTime eventDate,
                                                                                 LocalDateTime eventDate2,
                                                                                 Pageable pageable);
}
