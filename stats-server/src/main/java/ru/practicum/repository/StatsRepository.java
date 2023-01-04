package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.EndPointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndPointHit, Long> {

    List<EndPointHit> findAllByUriContainingIgnoreCaseAndTimeStampAfterAndTimeStampBefore(String uris,
                                                                                          LocalDateTime time1,
                                                                                          LocalDateTime time2);

    Integer countByUri(String name);
}