package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EndPointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndPointHit, Long> {

    List<EndPointHit> findAllByTimeStampBetween(LocalDateTime start, LocalDateTime end);

    List<EndPointHit> findAllByUriIn(List<String> uris);
}