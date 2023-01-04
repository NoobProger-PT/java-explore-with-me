package ru.practicum.services.private_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface PrivateParticipationRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEvent(long eventId);

    List<ParticipationRequest> findAllByRequester(long userId);

    Optional<ParticipationRequest> findByEventAndRequester(long eventId, long userId);
}
