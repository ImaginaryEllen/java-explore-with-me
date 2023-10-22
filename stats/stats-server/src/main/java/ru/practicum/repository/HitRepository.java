package ru.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {
    @EntityGraph(value = "Hit.entity", type = EntityGraph.EntityGraphType.LOAD)
    List<Hit> findAllByUriAndTimestampBetween(String uri, LocalDateTime start, LocalDateTime end);

    @EntityGraph(value = "Hit.entity", type = EntityGraph.EntityGraphType.LOAD)
    List<Hit> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @EntityGraph(value = "Hit.entity", type = EntityGraph.EntityGraphType.LOAD)
    Hit findAllByIpAndTimestampBetween(String ip, LocalDateTime start, LocalDateTime end);
}
