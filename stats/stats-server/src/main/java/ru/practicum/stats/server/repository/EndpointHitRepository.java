package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT e.uri AS uri, e.app AS app, COUNT(e.ip) AS hits " +
            "FROM EndpointHit AS e " +
            "WHERE :uris IS NULL OR e.uri IN :uris " +
            "AND e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, (e.uri)")
    List<ViewStats> getAllViews(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT e.uri AS uri, e.app AS app, COUNT(DISTINCT e.ip) AS hits " +
            "FROM EndpointHit AS e " +
            "WHERE :uris IS NULL OR e.uri IN :uris " +
            "AND e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri, e.ip")
    List<ViewStats> getUniqueViews(LocalDateTime start, LocalDateTime end, List<String> uris);
}
