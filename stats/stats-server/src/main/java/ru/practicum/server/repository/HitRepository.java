package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {
    @EntityGraph(value = "Hit.entity", type = EntityGraph.EntityGraphType.LOAD)
    List<Hit> findAllByUriAndTimestampBetween(String uri, LocalDateTime start, LocalDateTime end);

    @EntityGraph(value = "Hit.entity", type = EntityGraph.EntityGraphType.LOAD)
    List<Hit> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @EntityGraph(value = "Hit.entity", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select h from Hit as h where h.ip = ?1 and h.timestamp >= ?2 and h.timestamp <= ?3")
    List<Hit> findByIpAndByTimestampBetween(String ip, LocalDateTime start, LocalDateTime end);
}
