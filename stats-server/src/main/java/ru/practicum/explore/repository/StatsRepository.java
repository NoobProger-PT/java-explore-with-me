package ru.practicum.explore.repository;

import ru.practicum.explore.model.EndPointHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndPointHit, Long> {

    List<EndPointHit> findAllByUriContainingIgnoreCaseAndTimeStampAfterAndTimeStampBefore(String uris,
                                                                                          LocalDateTime time1,
                                                                                          LocalDateTime time2);

    Integer countByUri(String name);
}