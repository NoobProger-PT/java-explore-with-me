package ru.practicum.explore.services.private_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.event.location.model.Location;

import java.util.Optional;

public interface PrivateLocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(float lat, float lon);
}
