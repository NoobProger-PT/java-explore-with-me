package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndPointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndPointHit, Long> {

    List<EndPointHit> findAllByUriContainingIgnoreCaseAndTimeStampAfterAndTimeStampBefore(String uris,
                                                                                          LocalDateTime time1,
                                                                                          LocalDateTime time2);

    List<EndPointHit> findAllByTimeStampBetween(LocalDateTime start, LocalDateTime end);

    Integer countByUri(String name);

    @Query(value = "SELECT s.app, s.uri, COUNT(DISTINCT s.ip) FROM stats AS s WHERE s.timestamp BETWEEN ?1 and ?2",
            nativeQuery = true)
    List<EndPointHit> findByUniqIp1(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT * FROM stats AS s WHERE s.timestamp BETWEEN ?2 and ?3, ", nativeQuery = true)
    List<EndPointHit> findByUrisAndUniqIp(List<String> uris, LocalDateTime start, LocalDateTime end);
}