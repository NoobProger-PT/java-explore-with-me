package ru.practicum.services.private_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.location.model.Location;

import java.util.Optional;

public interface PrivateLocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(float lat, float lon);
}
